package ir.markazandroid.masteradvertiser.service;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.object.SHT;
import ir.markazandroid.masteradvertiser.object.Schedule;
import ir.markazandroid.masteradvertiser.object.Timeline;
import ir.markazandroid.masteradvertiser.util.date.Cron;
import ir.markazandroid.masteradvertiser.util.date.DateUtils;
import ir.markazandroid.masteradvertiser.util.date.DayInformation;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Coded by Ali on 9/21/2019.
 */
public class ScheduleService {


    private Context context;
    private Timer timer;
    private Schedule schedule;
    private DayInformation todayInformation;


    public ScheduleService(Context context){
        this.context=context;
        todayInformation=new DayInformation();
    }




    public void start(Schedule schedule){
        this.schedule=schedule;
        stop();

        Log.i(ScheduleService.class.getSimpleName(),String.format("Starting Schedule %s",schedule.getName()));
        refreshTimeLine();

        Date tomorrow = new Date(DateUtils.getTodayDate().getTime()+24*3600_000L);

        Log.i(ScheduleService.class.getSimpleName(),String.format("Next timeline refresh: %s",tomorrow.toString()));

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshTimeLine();
            }
        },tomorrow,24*3600_000L);




    }

    private void refreshTimeLine() {
        Log.i(ScheduleService.class.getSimpleName(),"Refreshing Timeline...");
        todayInformation.update();

        //priority 1 - Search for non cron date first of all
        Optional<SHT> shtOptional =StreamSupport.stream(schedule.getScheduleHasTimeLines())
                .filter(sht -> !sht.getCroned())
                .filter(sht ->{
                    String[] datesString =sht.getDates().split(",");
                    for (String dateString:datesString){
                        long dateTimestamp = Long.parseLong(dateString);
                        if (DateUtils.belongsToToday(dateTimestamp)){
                            return true;
                        }
                    }
                    return false;
                })
                .findAny();
        if (shtOptional.isPresent()){
            SHT sht = shtOptional.get();
            setActiveTimeline(sht.getTimeLine());
        }
        else {

            //priority 2 - Search for crons specified for this month
            List<SHT> nominatedShts = findThisMonthShts();
            shtOptional=findDaySht(nominatedShts);

            if (shtOptional.isPresent()){
                SHT sht = shtOptional.get();
                setActiveTimeline(sht.getTimeLine());
            }
            else {
                //priority 3 - Search for crons specified for all months
                nominatedShts = findAllMonthsShts();
                shtOptional=findDaySht(nominatedShts);

                if (shtOptional.isPresent()){
                    SHT sht = shtOptional.get();
                    setActiveTimeline(sht.getTimeLine());
                }
                else {
                    //TODO No Timeline is to show
                    Handler handler = new Handler(context.getMainLooper());
                    handler.post(()->Toast.makeText(context, "No Timeline", Toast.LENGTH_LONG).show());
                    Log.w(ScheduleService.class.getSimpleName(),"No Timeline To Show");
                }

            }

        }
    }

    private void setActiveTimeline(Timeline timeline){
        Log.d(ScheduleService.class.getSimpleName(),String.format("Setting timeline %s as active",timeline.getName()));
        getTimelineService().setActiveTimeline(timeline);
        //TODO setActiveTimeline
    }

    private Optional<SHT> findDaySht(List<SHT> nominatedShts){

        //priority 1 - Search for crons specified for this day of month
        Optional<SHT> shtOptional = findTodayDayOfMonthSht(nominatedShts);
        if (shtOptional.isPresent()) {
            return shtOptional;
        }
        else {
            //priority 2 - Search for crons specified for this day of week
            shtOptional = findTodayDayOfWeekSht(nominatedShts);
            if (shtOptional.isPresent()) {
                return shtOptional;
            }
            else{
                //priority 3 - Search for crons for all days
                shtOptional = findAllDaysSht(nominatedShts);
                if (shtOptional.isPresent()) {
                    return shtOptional;
                }
                else
                    return Optional.empty();
            }
        }
    }



    private List<SHT> findThisMonthShts(){
        return StreamSupport.stream(schedule.getScheduleHasTimeLines())
                .filter(SHT::getCroned)
                .filter(sht ->{
                    Cron cron = new Cron(sht.getCron());
                    return cron.matchesMonth(todayInformation.getMonth());
                })
                .collect(Collectors.toList());
    }

    private List<SHT> findAllMonthsShts(){
        return StreamSupport.stream(schedule.getScheduleHasTimeLines())
                .filter(SHT::getCroned)
                .filter(sht ->{
                    Cron cron = new Cron(sht.getCron());
                    return cron.isCronForAllMonths();
                })
                .collect(Collectors.toList());
    }

    private Optional<SHT> findTodayDayOfMonthSht(List<SHT> shts){
        return StreamSupport.stream(shts)
                .filter(sht ->{
                    Cron cron = new Cron(sht.getCron());
                    return cron.matchesDayOfMonth(todayInformation.getDayOfMonth());
                })
                .findAny();
    }

    private Optional<SHT> findTodayDayOfWeekSht(List<SHT> shts){
        return StreamSupport.stream(shts)
                .filter(sht ->{
                    Cron cron = new Cron(sht.getCron());
                    return cron.matchesDayOfWeek(todayInformation.getDayOfWeek());
                })
                .findAny();
    }

    private Optional<SHT> findAllDaysSht(List<SHT> shts){
        return StreamSupport.stream(shts)
                .filter(sht ->{
                    Cron cron = new Cron(sht.getCron());
                    return cron.isCronForAllDays();
                })
                .findAny();
    }


    private void stop(){
        Log.i(ScheduleService.class.getSimpleName(),"Stopping...");
        if (timer!=null)
            timer.cancel();

        timer=null;
    }


    private TimelineService getTimelineService(){
        return ((MasterAdvertiserApplication)context.getApplicationContext()).getTimelineService();
    }

}
