package ir.markazandroid.masteradvertiser.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.object.Series;
import ir.markazandroid.masteradvertiser.object.Timeline;
import ir.markazandroid.masteradvertiser.util.date.DateUtils;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Coded by Ali on 9/24/2019.
 */
public class TimelineService {

    private Context context;
    private ArrayList<Timer> timers;
    private Timeline timeline;

    public TimelineService(Context context) {
        this.context = context;
        timers = new ArrayList<>();
    }

    public void setActiveTimeline(Timeline timeline) {
        stop();

        Log.d(TimelineService.class.getSimpleName(),String.format("starting timeline %s",timeline.getName()));
        this.timeline=timeline;

        timers.addAll(StreamSupport.stream(timeline.getSeries())
                .map(series -> {
                    int seriesState = series.getState();
                    Log.d(TimelineService.class.getSimpleName(),String.format("Series State %d",seriesState));

                    if (seriesState == Series.NOT_STARTED_YET) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setActiveSeries(series);
                            }
                        }, series.getSeriesFrom());
                        return timer;
                    }
                    else if (seriesState== Series.WE_ARE_IN_IT){
                        setActiveSeries(series);
                        return null;
                    }
                    else
                        return null;
                })
                .filter(timer -> timer!=null)
                .collect(Collectors.toCollection(ArrayList::new)));
    }


    private void setActiveSeries(Series series) {
        Log.i(TimelineService.class.getSimpleName(), "Changing Series");
        getSeriesService().setActiveSeries(series);
    }

    private SeriesService getSeriesService(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getSeriesService();
    }

    private void stop() {
        Log.i(TimelineService.class.getSimpleName(), "Stopping");
        StreamSupport.stream(timers)
                .forEach(Timer::cancel);
        timers.clear();
    }

}
