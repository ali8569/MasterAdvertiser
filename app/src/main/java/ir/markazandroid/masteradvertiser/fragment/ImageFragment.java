package ir.markazandroid.masteradvertiser.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import ir.markazandroid.masteradvertiser.R;
import ir.markazandroid.masteradvertiser.downloader.MasterDownloader;
import ir.markazandroid.masteradvertiser.object.EFile;
import ir.markazandroid.masteradvertiser.views.playlist.data.Image;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends SelectiveFragment implements MasterDownloader.EFileFetchListener {

    private static final String IMAGE="image";
    private Image image;
    private File file;
    private boolean started;


    public ImageFragment() {
        Bundle bundle = new Bundle();
        setArguments(bundle);
        // Required empty public constructor
    }

    public ImageFragment image(Image image){
        getArguments().putSerializable(IMAGE,image);
        return this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        image= (Image) getArguments().getSerializable(IMAGE);
    }


    private void showImage() {

        Log.d("showImage",toString());
        //Picasso.get().setIndicatorsEnabled(true);
        RequestCreator requestCreator= Picasso.get()
                .load(file);

        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity()==null){
            int a=1;
        }
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch (image.getScaleType()){
            case "fitXY":
                requestCreator.fit();break;
            case "centerInside":
                requestCreator.resize(imageView.getWidth(),imageView.getHeight())
                        .centerInside();break;
            case "centerCrop":
                requestCreator.resize(imageView.getWidth(),imageView.getHeight())
                        .centerCrop();break;
            default:
                break;
        }
        requestCreator.into(imageView);
    }

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        imageView=view.findViewById(R.id.image);
        return view;
    }

    @Override
    protected void start() {
        started=true;
        try{
            if (file!=null)
                showImage();
            else
                requestFile();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestFile() {
        Log.e("Give me File","called");
        getDownloader().giveMeFile(image.geteFile(),this);
    }

    @Override
    protected void refresh() {
        start();
    }

    @Override
    protected void stop() {
        started=false;
        //getDownloader().removeListener(image.geteFile(),this);
    }

    @Override
    public void onEFileStatusChange(EFile eFile, int status, long downloaded, long total) {

    }

    @Override
    public void onFileReadyFromCache(File file) {
        Log.e("Give me File","Ready From Cache");
        this.file=file;
        if (started)
        showImage();
    }

    @Override
    public void onFileReadyFromNet(File file,boolean cached) {
        Log.e("Give me File","Ready from net");
        if (this.file==null || !cached){
            this.file=file;
            if (started)
            showImage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO image must not be null
        //if (image!=null)
        getDownloader().removeListener(image.geteFile(),this);
    }
}
