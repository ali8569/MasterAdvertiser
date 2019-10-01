package ir.markazandroid.masteradvertiser.service;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.media.dynamiclayoutinflator.DynamicLayoutInflator;
import ir.markazandroid.masteradvertiser.network.NetworkManager;
import ir.markazandroid.masteradvertiser.network.OnResultLoaded;
import ir.markazandroid.masteradvertiser.object.Campaign;
import ir.markazandroid.masteradvertiser.object.SeriesCampaign;
import ir.markazandroid.masteradvertiser.signal.Signal;
import ir.markazandroid.masteradvertiser.signal.SignalManager;
import ir.markazandroid.masteradvertiser.views.PlayListView;
import ir.markazandroid.masteradvertiser.views.Widget;
import java8.util.function.Consumer;
import java8.util.stream.StreamSupport;

/**
 * Coded by Ali on 6/22/2019.
 */
public class CampaignService {

    private ViewGroup mainContentHolder;
    private Context context;
    private Handler handler;
    private NetworkManager networkManager;
    private FragmentManager fragmentManager;
    private Campaign campaign;
    private ArrayList<Widget> shouldWaitWidgets;


    public CampaignService(Context context){
        this.context=context;
        networkManager=new NetworkManager.NetworkManagerBuilder()
                .from(context)
                .tag(toString())
                .build();
        handler = new Handler(context.getMainLooper());
    }

    public void setShowingProperties(ViewGroup mainContentHolder, FragmentManager fragmentManager) {
        this.mainContentHolder = mainContentHolder;
        this.fragmentManager = fragmentManager;
    }


    private void showCampaignOnMain(Campaign campaign) {
        disposeViews();

        Log.e(CampaignService.class.getSimpleName(), String.format("Showing Campaign %s",campaign.getName()));

        if (fragmentManager==null || mainContentHolder == null){
            Log.e(CampaignService.class.getSimpleName(), String.format("Holder isNull %s",campaign.getName()));
            return;
        }

        this.campaign = campaign;

        ViewGroup toShow = (ViewGroup) DynamicLayoutInflator.inflate(context, campaign.getAndroidData().getXml(), mainContentHolder);
        Iterator<String> iterator = campaign.getAndroidData().getExtras().keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject extra = (JSONObject) campaign.getAndroidData().getExtras().opt(key);
            Widget widget = (Widget) DynamicLayoutInflator.findViewByIdString(toShow, key);
            if (widget instanceof PlayListView)
                ((PlayListView) widget).setFragmentManager(fragmentManager);

            widget.init(extra);
            if (widget.shouldWaitUntilDone()){
                if (shouldWaitWidgets==null)
                    shouldWaitWidgets=new ArrayList<>();

                widget.setWidgetIsDoneListener(this::widgetSaysItsDone);

                shouldWaitWidgets.add(widget);
            }
        }

        mainContentHolder.removeAllViews();
        mainContentHolder.addView(toShow);
    }

    public void showCampaign(Campaign campaign){
        handler.post( () -> showCampaignOnMain(campaign));
    }

    private synchronized void widgetSaysItsDone(Widget widget){
        if (shouldWaitWidgets!=null && !shouldWaitWidgets.isEmpty()){
            shouldWaitWidgets.remove(widget);
            if (shouldWaitWidgets.isEmpty())
                getSeriesService().CSID();
        }
    }

    private void disposeViews() {
        if (mainContentHolder==null)
            return;

        if (mainContentHolder.getChildCount() < 1)
            return;

        ViewGroup main = ((ViewGroup) mainContentHolder.getChildAt(0));

        for (int i = 0; i < main.getChildCount(); i++) {
            View child = main.getChildAt(i);

            if (child instanceof Widget)
                ((Widget) child).dispose();
        }

        mainContentHolder.removeAllViews();

        Log.e(CampaignService.class.getSimpleName(), "Views Disposed");

    }


    public void fetchCampaigns(ArrayList<SeriesCampaign> seriesCampaigns,OnCampaignFetchFinishedListener listener){
        new Thread(() -> {
            for (SeriesCampaign seriesCampaign:seriesCampaigns){
                try {
                    Campaign campaign =networkManager.getCampaign(seriesCampaign.getCampaignId());
                    if (campaign==null) {
                        listener.failed();
                        return;
                    }
                    else
                        seriesCampaign.setCampaign(campaign);

                } catch (IOException e) {
                    e.printStackTrace();
                    listener.failed();
                    return;
                }
            }
            listener.onFinished(seriesCampaigns);
        }).start();
    }


    private void stopOnMain(){
        if (shouldWaitWidgets!=null) {
            shouldWaitWidgets.clear();
            shouldWaitWidgets=null;
        }
        disposeViews();
        setShowingProperties(null,null);
    }

    public void stop(){
        handler.post(this::stopOnMain);
    }

    private SeriesService getSeriesService(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getSeriesService();
    }



    public interface OnCampaignFetchFinishedListener{
        void onFinished(ArrayList<SeriesCampaign> fetchedSeriesCampaigns);
        void failed();
    }
}