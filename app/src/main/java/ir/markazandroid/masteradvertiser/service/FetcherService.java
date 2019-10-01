package ir.markazandroid.masteradvertiser.service;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.downloader.MasterDownloader;
import ir.markazandroid.masteradvertiser.network.NetworkManager;
import ir.markazandroid.masteradvertiser.network.OnResultLoaded;
import ir.markazandroid.masteradvertiser.object.Schedule;
import ir.markazandroid.masteradvertiser.object.SeriesCampaign;
import ir.markazandroid.masteradvertiser.util.Cache;
import java8.util.stream.StreamSupport;

/**
 * Coded by Ali on 9/24/2019.
 */
public class FetcherService {

    private Timer fetchTimer;
    private NetworkManager networkManager;
    private Context context;
    private long lastUpdate;
    private Handler handler;
    private boolean started;


    public FetcherService(Context context){
        this.context=context;
        handler=new Handler(context.getMainLooper());
        networkManager=new NetworkManager.NetworkManagerBuilder()
                .from(context)
                .tag(toString())
                .build();
    }

    public void start(){
        stop();

        started=true;
        Log.i(FetcherService.class.getSimpleName(),"Starting...");

        Schedule cachedSchedule = getCache().getCachedSchedule();
        if (cachedSchedule!=null) {
            lastUpdate = cachedSchedule.getLastUpdate();
            Log.i(FetcherService.class.getSimpleName(),String.format("Last Schedule %d",lastUpdate));
            getScheduleService().start(cachedSchedule);
        }

        fetchTimer=new Timer();
        fetchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                networkManager.getSchedule(lastUpdate, new OnResultLoaded.AvailableListener<Schedule>() {
                    @Override
                    public void onAvailable(Schedule successResult) {
                        Log.i(FetcherService.class.getSimpleName(),String.format("New schedule %d available",successResult.getLastUpdate()));
                        setLastUpdate(successResult.getLastUpdate());
                        prepareAndLaunchSchedule(successResult);
                    }

                    @Override
                    public void onNotAvailable() {
                        Log.d(FetcherService.class.getSimpleName(),"No schedule update available");
                        //TODO do nothing?
                    }

                    @Override
                    public void failed(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        },0,60_000);

    }

    private void prepareAndLaunchSchedule(Schedule schedule) {
        Log.i(FetcherService.class.getSimpleName(),String.format("Preparing Schedule %s...",schedule.getName()));
        ArrayList<SeriesCampaign> seriesCampaigns = new ArrayList<>();
        StreamSupport.stream(schedule.getScheduleHasTimeLines())
                .map(sht -> sht.getTimeLine().getSeries())
                .forEach(series -> StreamSupport.stream(series)
                        .forEach(series1 -> seriesCampaigns.addAll(series1.getCampaigns())));

        getCampaignService().fetchCampaigns(seriesCampaigns, new CampaignService.OnCampaignFetchFinishedListener() {
            @Override
            public void onFinished(ArrayList<SeriesCampaign> fetchedSeriesCampaigns) {
                Log.d(FetcherService.class.getSimpleName(),"Campaign Fetch Completed");
                getCache().cacheSchedule(schedule);
                getScheduleService().start(schedule);
            }

            @Override
            public void failed() {
                Log.e(FetcherService.class.getSimpleName(),"Failed to fetch Campaigns, retrying...");
                prepareAndLaunchSchedule(schedule);
            }
        });

    }

    public void stop(){
        Log.i(FetcherService.class.getSimpleName(),"Stopping...");
        if (fetchTimer!=null)
            fetchTimer.cancel();
        started=false;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    private CampaignService getCampaignService(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getCampaignService();
    }

    private ScheduleService getScheduleService(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getScheduleService();
    }

    private MasterDownloader getMasterDownloader(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getMasterDownloader();
    }

    private Cache getCache(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getCache();
    }

    public boolean isStarted() {
        return started;
    }
}
