package ir.markazandroid.masteradvertiser.util;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.fragment.ImageFragment;
import ir.markazandroid.masteradvertiser.fragment.VideoFragment;
import ir.markazandroid.masteradvertiser.views.playlist.data.DataEntity;
import ir.markazandroid.masteradvertiser.views.playlist.data.Image;
import ir.markazandroid.masteradvertiser.views.playlist.data.Video;

/**
 * Coded by Ali on 10/21/2018.
 */
public class ContentAdapter {

    private FragmentManager fragmentManager;
    private Queue<DataEntity> contents;
    private Queue<Fragment> fragments;
    private View container;
    private Timer timer;
    private Fragment currentFragment;
    private Handler handler;


    public ContentAdapter(Context context, FragmentManager fragmentManager, ArrayList<DataEntity> contents, View container) {
        this.fragmentManager = fragmentManager;
        this.contents =new LinkedList<>(contents);
        this.container = container;
        this.handler=new Handler(context.getMainLooper());
        init();
    }

    private void init(){

        fragments=new LinkedList<>();

        for(DataEntity data:contents){
            Fragment fragment;

            switch (data.getDataType()){
                case Image.DATA_TYPE_IMAGE:
                    fragment=new ImageFragment().image((Image) data);break;

                case Video.DATA_TYPE_VIDEO:
                    fragment=new VideoFragment().videoUrl((Video) data)
                            .videoStateChangeListener(this::onVideoFinish);break;

                    default: fragment=new Fragment();

            }
            fragments.add(fragment);
        }
    }

    public void gotoNext(){
        handler.post(() ->{
            if (timer!=null) timer.cancel();

          //  if (!contents.isEmpty()) {
           //     schedule(contents.iterator());
            //    return;
           // }

            if (contents.isEmpty()) return;

            DataEntity content = contents.poll();
            contents.add(content);
            makeView(content);


            if (content.getDataType().equals(Image.DATA_TYPE_IMAGE) && contents.size()>1){
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gotoNext();
                    }
                },content.getDuration()*1000);
            }
        });
    }

    private void makeView(DataEntity content) {
        String name = generateFragmentTag(content);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (fragment!=null && fragment.equals(currentFragment)) return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,android.R.anim.fade_in,android.R.anim.fade_out);
        if (currentFragment!=null){
            transaction.detach(currentFragment);
        }
        if (fragment != null) {
            transaction.attach(fragment);
        } else {
            fragment = fragments.poll();
            fragments.add(fragment);
            transaction.add(container.getId(), fragment, generateFragmentTag(content));
        }
        currentFragment=fragment;
        //TODO warn
        if (fragmentManager.isStateSaved())
            dispose();
        else
            transaction.commit();
    }

    private String generateFragmentTag(DataEntity content){
        return container.getId()+"."+content.hashCode();
    }

    public void dispose(){
        if (timer!=null) timer.cancel();
        timer=null;
    }

    private void onVideoFinish(Fragment frag) {
        if (frag.equals(currentFragment)){
            gotoNext();
        }


    }
}
