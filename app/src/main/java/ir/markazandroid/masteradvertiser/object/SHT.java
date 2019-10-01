package ir.markazandroid.masteradvertiser.object;

import java.io.Serializable;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Created by Ali on 4/8/2019.
 */
public class SHT implements Serializable {
    private int scheduleId;
    private long timelineId;
    private boolean croned;
    private String cron;
    private Schedule schedule;
    private Timeline timeLine;
    private long shtId;
    private String dates;

    @JSON
    public long getShtId() {
        return shtId;
    }

    public void setShtId(long shtId) {
        this.shtId = shtId;
    }

    @JSON
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    @JSON
    public long getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(long timeLineId) {
        this.timelineId = timeLineId;
    }

    @JSON
    public boolean getCroned() {
        return croned;
    }

    public void setCroned(boolean croned) {
        this.croned = croned;
    }

    @JSON
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }


    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @JSON(classType = JSON.CLASS_TYPE_OBJECT, clazz = Timeline.class)
    public Timeline getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(Timeline timeLine) {
        this.timeLine = timeLine;
    }

    @JSON
    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }
}
