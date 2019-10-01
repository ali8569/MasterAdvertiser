package ir.markazandroid.masteradvertiser.service;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.object.Campaign;
import ir.markazandroid.masteradvertiser.object.Series;
import ir.markazandroid.masteradvertiser.object.SeriesCampaign;
import ir.markazandroid.masteradvertiser.util.date.DateUtils;
import java8.util.function.Consumer;
import java8.util.stream.StreamSupport;

/**
 * Coded by Ali on 9/24/2019.
 */
public class SeriesService {

    private Context context;
    private Series series;
    private SeriesCampaign currentSeriesCampaign;
    private Timer campaignChangeTimer;

    public SeriesService(Context context){
        this.context=context;
    }


    public void setActiveSeries(Series series) {
        this.series = series;
        Log.i(SeriesService.class.getSimpleName(), "Changing Series");

        SeriesCampaign determinedCampaign = determineCurrentCampaign();

        if (determinedCampaign==null){
            Log.e(SeriesService.class.getSimpleName(), "No Campaign Found! Are You Kidding Me ?!");
        }
        else
            setActiveCampaign(determinedCampaign);

    }

    private SeriesCampaign determineCurrentCampaign(){
        if (series.getState()!=Series.WE_ARE_IN_IT)
            return null;

        Date seriesFrom = series.getSeriesFrom();
        Date now = new Date();

        for (SeriesCampaign seriesCampaign:series.getCampaigns()){
            Date campaignEnd =seriesCampaign.getEndCampaign(seriesFrom);

            if (campaignEnd.after(now))
                return seriesCampaign;
            else
                seriesFrom=campaignEnd;
        }

        //No Campaign matched ! should not happen
        return null;

    }

    private Date determineExactEndCampaign(SeriesCampaign till){
        Date seriesFrom = series.getSeriesFrom();

        int index = getIndex(till);

        for (int i = 0; i < index+1; i++) {
            SeriesCampaign seriesCampaign = series.getCampaigns().get(i);
            seriesFrom =seriesCampaign.getEndCampaign(seriesFrom);
        }
        return seriesFrom;
    }

    private int getIndex(SeriesCampaign till) {
        int index = series.getCampaigns().indexOf(till);
        if (index==-1)
            throw new RuntimeException("Campaign not Found");
        return index;
    }

    private void setActiveCampaign(SeriesCampaign seriesCampaign){
        currentSeriesCampaign=seriesCampaign;

        Log.e(SeriesService.class.getSimpleName(), String.format("Setting Active Campaign %s",seriesCampaign.getCampaign().getName()));


        if (currentSeriesCampaign.isUserTimed()){
            if (campaignChangeTimer!=null)
                campaignChangeTimer.cancel();

            Date timerDate =determineExactEndCampaign(currentSeriesCampaign);
            campaignChangeTimer=new Timer();
            campaignChangeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    endCampaign();
                }
            },determineExactEndCampaign(currentSeriesCampaign));

            Log.e(SeriesService.class.getSimpleName(), String.format("Campaign is user Timed: %s",timerDate.toString()));

        }

        getCampaignService().showCampaign(currentSeriesCampaign.getCampaign());
    }

    public void CSID(){
        if (!currentSeriesCampaign.isUserTimed())
            endCampaign();
    }

    private void endCampaign(){
        int index = getIndex(currentSeriesCampaign);

        if (index+1==series.getCampaigns().size()){
            //TODO Series Ended
            Log.e(SeriesService.class.getSimpleName(), "Series Ended");
        }
        else
            setActiveCampaign(series.getCampaigns().get(index+1));
    }


    private CampaignService getCampaignService(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getCampaignService();
    }
}
