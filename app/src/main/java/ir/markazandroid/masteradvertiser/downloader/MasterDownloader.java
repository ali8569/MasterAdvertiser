package ir.markazandroid.masteradvertiser.downloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.db.DataBase;
import ir.markazandroid.masteradvertiser.network.NetStatics;
import ir.markazandroid.masteradvertiser.object.EFile;
import ir.markazandroid.masteradvertiser.signal.Signal;
import ir.markazandroid.masteradvertiser.signal.SignalManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Coded by Ali on 5/1/2018.
 */
public class MasterDownloader {

    public static final int STATUS_IDEAL = 1;
    public static final int STATUS_DOWNLOADING = 2;

    public static final int EFILE_STATUS_IDEAL = 1;
    public static final int EFILE_STATUS_DOWNLOADING = 2;

    public static final int EFILE_STATUS_FINISHED = 3;
    public static final int EFILE_STATUS_FINISHED_CAHCHE = 4;

    public static final int EFILE_STATUS_FAILED = 5;
    public static final int EFILE_STATUS_FAILED_NO_RETRY = 7;
    public static final int EFILE_STATUS_CANCELED = 6;


    public interface ProcessMonitor {
        void onFileManagerStatusChange(int status);
    }

    public interface EFileFetchListener {
        /**
         * @param eFile
         * @param status     one of
         *                   {@link #EFILE_STATUS_IDEAL}
         *                   {@link #EFILE_STATUS_DOWNLOADING}
         *                   {@link #EFILE_STATUS_FINISHED}
         *                   {@link #EFILE_STATUS_FINISHED_CAHCHE}
         *                   {@link #EFILE_STATUS_FAILED}
         *                   {@link #EFILE_STATUS_FAILED_NO_RETRY}
         *                   {@link #EFILE_STATUS_CANCELED}
         * @param downloaded
         * @param total
         */
        void onEFileStatusChange(EFile eFile, int status, long downloaded, long total);

        void onFileReadyFromCache(File file);

        void onFileReadyFromNet(File file, boolean cached);
    }


    private Context context;
    private List<ProcessMonitor> processMonitors;
    private boolean isCancelled = false;
    private String contentDirectory;
    private MappedLinkedBlockingQueue<String, FileTask> eFileQueue;
    private BlockingMap<String, FileTask> downloadingTasks;
    private Handler handler;
    private String tempDir;
    private final Lock transitionLock;

    private ThreadPoolExecutor executor;

    public MasterDownloader(Context context) {
        this.context = context;
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        eFileQueue = new MappedLinkedBlockingQueue<>();

        String root = Environment.getExternalStorageDirectory().toString();
        contentDirectory = mkdirs(root + "/masteradvertiser/files");
        tempDir = mkdirs(root + "/masteradvertiser/temp");

        handler = new Handler(context.getMainLooper());

        processMonitors = new ArrayList<>();

        downloadingTasks = new BlockingMap<>();

        transitionLock = new ReentrantLock();

        Log.d("Downloader","init");
    }


    private String mkdirs(String path) {
        File f = new File(path);
        f.mkdirs();
        return f.toString();
    }

    public boolean isCancelled() {
        return isCancelled;
    }


    private void checkFromDb(FileTask fileTask) {
        SQLiteDatabase db = ((MasterAdvertiserApplication) context.getApplicationContext()).getDataBase().getWritableDatabase();
        Cursor c = db.query(DataBase.LinkTable.TABLE_NAME, new String[]{DataBase.LinkTable.ETAG},
                DataBase.LinkTable.LINK + "=?",
                new String[]{fileTask.getId()}, null, null, null);
        String ETag = null;
        if (c.moveToFirst()) ETag = c.getString(c.getColumnIndex(DataBase.LinkTable.ETAG));
        c.close();
        if (ETag != null) {
            File file = new File(contentDirectory + "/" + MasterDownloader.extractFilename(fileTask.getId()));
            if (!file.exists()) {
                ETag = null;
                db.delete(DataBase.LinkTable.TABLE_NAME, DataBase.LinkTable.LINK + "=?", new String[]{fileTask.getId()});
            }
        }

        fileTask.setETag(ETag);
    }

    public void start() {
        DownloadRunnable downloadRunnable = new DownloadRunnable();
        executor.execute(downloadRunnable);

    }

    public static String extractFilename(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }

    public void setCancelled() {
        isCancelled = true;
        executor.shutdownNow();
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    //Will return without block
    public void giveMeFile(final EFile eFile, final EFileFetchListener fetchListener) {
        executor.execute(() -> {
            while (true) {
                try {
                    transitionLock.lock();
                    FileTask fileTask;
                    fileTask = eFileQueue.find(new FileTask(eFile).getId());
                    if (fileTask == null)
                        fileTask = downloadingTasks.get(new FileTask(eFile).getId());

                    if (fileTask != null)
                        fileTask.getListeners().add(fetchListener);
                    else {
                        fileTask = new FileTask(eFile);
                        fileTask.getListeners().add(fetchListener);
                        addToQueue(fileTask);
                    }
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    transitionLock.unlock();
                }
            }
        });
    }

    public void removeListener(EFile eFile, EFileFetchListener listener) {
        try {
            transitionLock.lock();
            FileTask fileTask;
            fileTask = eFileQueue.find(new FileTask(eFile).getId());
            if (fileTask == null)
                fileTask = downloadingTasks.get(new FileTask(eFile).getId());

            if (fileTask != null)
                fileTask.getListeners().remove(listener);
        } finally {
            transitionLock.unlock();
        }
    }

    private FileTask getNewTask() {
        while (true) {
            try {
                FileTask fileTask = eFileQueue.take();
                transitionLock.lock();
                downloadingTasks.put(fileTask.getId(), fileTask);
                return fileTask;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                transitionLock.unlock();
            }
        }
    }

    private void backToQueue(FileTask fileTask) {
        while (true) {
            try {
                transitionLock.lock();
                downloadingTasks.remove(fileTask.getId());
                addToQueue(fileTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                transitionLock.unlock();
            }
        }
    }

    private void addToQueue(FileTask fileTask) throws InterruptedException {
        eFileQueue.put(fileTask);
        broadcastTaskStatus(fileTask, EFILE_STATUS_IDEAL, 0, 0);
    }

    private void lockAll() {
        eFileQueue.lock();
        downloadingTasks.lock();
    }

    private void unLockAll() {
        eFileQueue.unlock();
        downloadingTasks.unlock();
    }

    private class DownloadRunnable implements Runnable {

        @Override
        public void run() {

            Log.d("Downloader","start");

            while (true) {
                try {
                    Log.d("Downloader","waiting for file");
                    FileTask task = getNewTask();
                    Log.d("Downloader","file: "+task.getId());
                    checkFromDb(task);
                    checkNetFile(task);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkNetFile(FileTask fileTask) throws InterruptedException {
        if (fileTask.ETag != null) {
            broadcastFileReadyFromCache(fileTask);
        }
        int result = downloadFile(fileTask);
        switch (result) {

            case DOWNLOAD_RESULT_CANCELLED:
                broadcastTaskStatus(fileTask, EFILE_STATUS_CANCELED, -1, -1);
                break;

            case DOWNLOAD_RESULT_HTTP_ERROR:
                broadcastTaskStatus(fileTask, EFILE_STATUS_FAILED_NO_RETRY, -1, -1);
                break;

            case DOWNLOAD_RESULT_NO_NET:
            case DOWNLOAD_RESULT_IO_ERROR:
                broadcastTaskStatus(fileTask, EFILE_STATUS_FAILED, -1, -1);
                backToQueue(fileTask);
                Thread.sleep(1000);
                break;

            case DOWNLOAD_RESULT_DOWNLOADED:
                broadcastFileReadyDownload(fileTask, true);
                break;
            case DOWNLOAD_RESULT_CORRECT:
                broadcastFileReadyDownload(fileTask, false);
                break;

        }
    }

    private void broadcastTaskStatus(final FileTask fileTask, int status, long downloaded, long total) {
        for (EFileFetchListener listener : fileTask.getListeners()) {
            handler.post(() -> {
                        try {
                            listener.onEFileStatusChange(fileTask.eFile, status, downloaded, total);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }

    private void broadcastFileReadyFromCache(FileTask fileTask) {
        for (EFileFetchListener listener : fileTask.getListeners()) {
            handler.post(() -> {
                try {
                    File file = getFile(fileTask.getEFile());
                    listener.onEFileStatusChange(fileTask.getEFile(), EFILE_STATUS_FINISHED_CAHCHE, 0, file.length());
                    ;
                    listener.onFileReadyFromCache(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void broadcastFileReadyDownload(FileTask fileTask, boolean freshDownload) {
        for (EFileFetchListener listener : fileTask.getListeners()) {
            handler.post(() -> {
                try {
                    File file = getFile(fileTask.getEFile());
                    listener.onEFileStatusChange(fileTask.getEFile(), EFILE_STATUS_FINISHED,
                            freshDownload ? file.length() : 0, file.length());
                    listener.onFileReadyFromNet(file, !freshDownload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static String getPathUrl(String url) {
        return url.substring(0, url.lastIndexOf("/") + 1);
    }

    public File getFile(EFile eFile) {
        return new File(contentDirectory, eFile.geteFileId());
    }

    private static String getEncodedUrl(String url) {
        try {
            String s = getPathUrl(url) + URLEncoder.encode(extractFilename(url), "UTF-8");
            Log.e("url", s);
            return s;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final int DOWNLOAD_RESULT_NO_NET = -1;
    private static final int DOWNLOAD_RESULT_CANCELLED = -2;
    private static final int DOWNLOAD_RESULT_HTTP_ERROR = -3;
    private static final int DOWNLOAD_RESULT_IO_ERROR = -4;

    private static final int DOWNLOAD_RESULT_DOWNLOADED = 1;
    private static final int DOWNLOAD_RESULT_CORRECT = 2;


    private int downloadFile(FileTask fileTask) {
        if (!isNetworkConnected()) {
            return DOWNLOAD_RESULT_NO_NET;
        }
        Log.e("link", fileTask.getId());

        //TODO make link
        Request.Builder request = new Request.Builder()
                .url(NetStatics.DOMAIN+"/data/read?eFileId="+fileTask.getEFile().geteFileId())
                .get();
        File tempFile = new File(tempDir, fileTask.getId());
        boolean isResume = tempFile.exists();

        if (isResume) {
            request.addHeader("Range", "bytes=" + tempFile.length() + "-");
        }
        Response response = null;
        try {
            response = getClient().newCall(request.build()).execute();
            getSignalManager().sendMainSignal(new Signal("NET", Signal.DOWNLOADER_NETWORK));
            if (isCancelled) return DOWNLOAD_RESULT_CANCELLED;
            if (response.isSuccessful()) {
                if (!response.header("ETag", "").equals(fileTask.getETag())) {
                    doDownload(fileTask, response.body(),
                            new File(contentDirectory + "/" + fileTask.getId()),
                            isResume ? tempFile.length() + response.body().contentLength() : response.body().contentLength()
                            , isResume);
                    fileTask.ETag = response.header("ETag");
                    saveToDb(fileTask);
                    return DOWNLOAD_RESULT_DOWNLOADED;
                } else {
                    Log.e("File: " + fileTask.getId(), "Correct");
                    return DOWNLOAD_RESULT_CORRECT;
                }
            } else {
                Log.e("File:" + fileTask.getId(), "download failed status:" + response.code());
                return DOWNLOAD_RESULT_HTTP_ERROR;
            }
        } catch (IOException e) {
            getSignalManager().sendMainSignal(new Signal("NO_NET", Signal.DOWNLOADER_NO_NETWORK));
            e.printStackTrace();
            Log.e("File" + fileTask.getId(), "download failed");
            return DOWNLOAD_RESULT_IO_ERROR;
        } finally {
            if (response != null)
                response.close();
        }
    }

    private void doDownload(FileTask fileTask, ResponseBody body, File out, long len, boolean isResume) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        int lastChange;
        long total = 0;
        long max = len;
        long dled = 0;
        File tempFile = new File(tempDir, out.getName());
        BufferedSource source = body.source();

        /*if(isResume)
            source.skip(tempFile.length());*/

        BufferedInputStream in = new BufferedInputStream(body.byteStream());
        FileOutputStream outputStream;

        if (isResume) {
            outputStream = new FileOutputStream(tempFile, true);
            total = tempFile.length();
        } else {
            outputStream = new FileOutputStream(tempFile, false);
        }

        try {
            while ((length = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                total += length;
                final long downloaded = total;
                if (downloaded - dled >= (0.1f * 1024f * 1024f) || downloaded == 0 || downloaded == max) {
                    dled = downloaded;
                    //Log.e("Downloaded",out.getName()+"... "+downloaded+"/"+max+"--"+(int)((downloaded/max)*100f));
                    broadcastTaskStatus(fileTask, EFILE_STATUS_DOWNLOADING, downloaded, max);
                    // processMonitor.onProcess(out.getName()+"... "+downloaded+"/"+max,(int)((downloaded/max)*100f));
                }
            }

            outputStream.flush();
            outputStream.close();
            moveFileTo(tempFile, out);
        } finally {
            outputStream.flush();
            outputStream.close();
        }


    }

    private OkHttpClient getClient() {
        return ((MasterAdvertiserApplication) context.getApplicationContext()).getNetworkClient().getClient();
    }


    private boolean copyFileToFolder(File from, String toFolder) {
        try {
            FileUtils.copyFileToDirectory(from, new File(toFolder));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void saveToDb(FileTask fileTask) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBase.LinkTable.LINK, fileTask.getId());
        contentValues.put(DataBase.LinkTable.ETAG, fileTask.getETag());
        SQLiteDatabase db = ((MasterAdvertiserApplication) context.getApplicationContext()).getDataBase().getWritableDatabase();
        db.delete(DataBase.LinkTable.TABLE_NAME, DataBase.LinkTable.LINK + "=?", new String[]{fileTask.getId()});
        db.insert(DataBase.LinkTable.TABLE_NAME, null, contentValues);
    }

    private boolean moveFileTo(File from, File to) {
        try {
            FileUtils.moveFile(from, to);
            return true;
        } catch (FileExistsException e) {
            try {
                FileUtils.forceDelete(to);
                return moveFileTo(from, to);
            } catch (IOException e1) {
                e1.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected SignalManager getSignalManager() {
        return ((MasterAdvertiserApplication) context.getApplicationContext()).getSignalManager();
    }

    private boolean isNetworkConnected() {
        return ((MasterAdvertiserApplication) context.getApplicationContext()).isInternetConnected();
    }

    private static class FileTask implements UniqueIdentifier<String> {
        private EFile eFile;
        private Set<EFileFetchListener> listeners;
        private String ETag;

        public FileTask(EFile eFile) {
            this.eFile = eFile;
            listeners = Collections.synchronizedSet(new HashSet<>());
        }

        public EFile getEFile() {
            return eFile;
        }


        public String getETag() {
            return ETag;
        }

        public void setETag(String ETag) {
            this.ETag = ETag;
        }

        @Override
        public String getId() {
            return eFile.geteFileId();
            /*try {
                return URLEncoder.encode(eFile.geteFileId(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return eFile.geteFileId();
            }*/
        }

        public Set<EFileFetchListener> getListeners() {
            return listeners;
        }
    }
}
