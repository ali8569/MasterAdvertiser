package ir.markazandroid.masteradvertiser.media;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.media.dynamiclayoutinflator.DynamicLayoutInflator;
import ir.markazandroid.masteradvertiser.network.NetworkManager;
import ir.markazandroid.masteradvertiser.network.OnResultLoaded;
import ir.markazandroid.masteradvertiser.object.Campaign;
import ir.markazandroid.masteradvertiser.views.Widget;
import ir.markazandroid.masteradvertiser.views.PlayListView;

/**
 * Coded by Ali on 6/22/2019.
 */
public class CampaignService {

    private ViewGroup mainContentHolder;
    private Context context;
    private Handler handler;
    private Timer fetchTimer;
    private NetworkManager networkManager;
    private FragmentManager fragmentManager;


    public CampaignService(ViewGroup mainContentHolder, NetworkManager networkManager, FragmentManager fragmentManager) {
        this.mainContentHolder = mainContentHolder;
        context=mainContentHolder.getContext();
        handler=new Handler(context.getMainLooper());
        this.networkManager=networkManager;
        this.fragmentManager=fragmentManager;
    }

    public void start(){
        if (fetchTimer!=null)
            fetchTimer.cancel();
        fetchTimer=new Timer();
        fetchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                networkManager.getCampaign(new OnResultLoaded<Campaign>() {
                    @Override
                    public void loaded(Campaign result) {
                        showCampaign(result);
                    }

                    @Override
                    public void failed(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        },0/*,30_000*/);
    }

    private void showCampaign(Campaign campaign){
        ViewGroup toShow = (ViewGroup) DynamicLayoutInflator.inflate(context,campaign.getAndroidData().getXml(),mainContentHolder);
        Iterator<String> iterator = campaign.getAndroidData().getExtras().keys();
        while (iterator.hasNext()){
            String key =iterator.next();
            JSONObject extra = (JSONObject) campaign.getAndroidData().getExtras().opt(key);
            Widget widget = (Widget) DynamicLayoutInflator.findViewByIdString(toShow,key);
            if (widget instanceof PlayListView)
                ((PlayListView) widget).setFragmentManager(fragmentManager);
            widget.init(extra);
        }

        mainContentHolder.removeAllViews();
        mainContentHolder.addView(toShow);
    }
}