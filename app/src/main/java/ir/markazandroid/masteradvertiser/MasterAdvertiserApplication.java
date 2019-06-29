package ir.markazandroid.masteradvertiser;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import ir.markazandroid.masteradvertiser.aidl.PoliceBridge;
import ir.markazandroid.masteradvertiser.aidl.PoliceHandlerHelper;
import ir.markazandroid.masteradvertiser.db.DataBase;
import ir.markazandroid.masteradvertiser.downloader.MasterDownloader;
import ir.markazandroid.masteradvertiser.network.JSONParser.Parser;
import ir.markazandroid.masteradvertiser.network.NetworkClient;
import ir.markazandroid.masteradvertiser.object.Campaign;
import ir.markazandroid.masteradvertiser.object.EFile;
import ir.markazandroid.masteradvertiser.object.ErrorObject;
import ir.markazandroid.masteradvertiser.object.FieldError;
import ir.markazandroid.masteradvertiser.object.Phone;
import ir.markazandroid.masteradvertiser.object.User;
import ir.markazandroid.masteradvertiser.object.Version;
import ir.markazandroid.masteradvertiser.signal.Signal;
import ir.markazandroid.masteradvertiser.signal.SignalManager;
import ir.markazandroid.masteradvertiser.signal.SignalReceiver;
import ir.markazandroid.masteradvertiser.util.PreferencesManager;
import ir.markazandroid.masteradvertiser.views.WebPageView;
import ir.markazandroid.masteradvertiser.views.playlist.data.Data;
import ir.markazandroid.masteradvertiser.views.playlist.data.DataEntity;
import ir.markazandroid.masteradvertiser.views.playlist.data.Image;
import ir.markazandroid.masteradvertiser.views.PlayListView;
import ir.markazandroid.masteradvertiser.views.playlist.data.Video;

/**
 * Coded by Ali on 6/19/2019.
 */
public class MasterAdvertiserApplication extends Application implements SignalReceiver {

    public static final String SHARED_PREFRENCES = "shared_pref";

    private NetworkClient networkClient;
    private SignalManager signalManager;
    private Parser parser;
    private PreferencesManager preferencesManager;
    private DataBase dataBase;
    private PoliceBridge policeBridge;
    private boolean isInternetConnected = false;
    private MasterDownloader masterDownloader;

    @Override
    public void onCreate() {
        super.onCreate();

        getPoliceBridge();
        //installApp();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/advertiser/image");
        myDir.mkdirs();
        Picasso picasso =new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(myDir,200_000_000))
                .build();
        Picasso.setSingletonInstance(picasso);
        getSignalManager().addReceiver(this);

    }

    public Parser getParser() {
        if (parser==null) {
            try {
                parser = new Parser();
                parser.addClass(ErrorObject.class);
                parser.addClass(User.class);
                parser.addClass(Phone.class);
                parser.addClass(FieldError.class);
                parser.addClass(Version.class);
                parser.addClass(Campaign.class);
                parser.addClass(Campaign.AndroidData.class);
                parser.addClass(EFile.class);

                //PlayList
                parser.addClass(PlayListView.Extras.class);
                parser.addClass(Data.class);
                parser.addClass(DataEntity.class);
                parser.addWithSuperClasses(Image.class);
                parser.addWithSuperClasses(Video.class);

                //webPage
                parser.addClass(WebPageView.WebViewExtras.class);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }
        return parser;
    }

    public DataBase getDataBase(){
        if (dataBase==null) dataBase=new DataBase(this);
        return dataBase;
    }

    @Override
    public boolean onSignal(Signal signal) {
        if (signal.getType() == Signal.SIGNAL_LOGIN) {
            // setUser((User) signal.getExtras());
            Log.e(MasterAdvertiserApplication.this.toString(), "login signal received " /*+ user.getUsername()*/);
            return true;
        } else if (signal.getType() == Signal.SIGNAL_LOGOUT) {
            Log.e(MasterAdvertiserApplication.this.toString(), "logout signal received ");
            //getNetworkClient().deleteCookies();
            //DeleteToken deleteToken = new DeleteToken();
            //deleteToken.execute();
            //setUser(null);
            return true;
        }
        else if (signal.getType()==Signal.START_MAIN_ACTIVITY){
           /* if (getFrontActivity()==null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }*/
        }
        else if (signal.getType()==Signal.OPEN_SOCKET_HEADER_RECEIVED){
        }else if (signal.getType()==Signal.DOWNLOADER_NO_NETWORK){
            isInternetConnected=false;
        }else if (signal.getType()==Signal.DOWNLOADER_NETWORK){
            isInternetConnected=true;
        }
        return false;
    }

    public PreferencesManager getPreferencesManager() {
        if (preferencesManager==null) preferencesManager=new PreferencesManager(getSharedPreferences(SHARED_PREFRENCES,MODE_PRIVATE));
        return preferencesManager;
    }

    public NetworkClient getNetworkClient(){
        if (networkClient==null){
            networkClient=new NetworkClient(this);
        }
        return networkClient;
    }

    public SignalManager getSignalManager() {
        if (signalManager == null) signalManager = new SignalManager(this);
        return signalManager;
    }

    public boolean isInternetConnected() {
        return isInternetConnected;
    }

    public PoliceBridge getPoliceBridge() {
        if (policeBridge==null){
            policeBridge=new PoliceHandlerHelper(this,getSignalManager());
        }
        return policeBridge;
    }

    public MasterDownloader getMasterDownloader() {
        if (masterDownloader==null){
            masterDownloader=new MasterDownloader(this);
            masterDownloader.start();
        }
        return masterDownloader;
    }
}
