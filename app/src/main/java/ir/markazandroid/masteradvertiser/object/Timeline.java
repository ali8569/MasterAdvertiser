package ir.markazandroid.masteradvertiser.object;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Created by Ali on 4/8/2019.
 */
public class Timeline implements Serializable {
    private long timelineId;
    private long userId;

    private ArrayList<Series> series;
    private long createTime;
    private String name;

    @JSON
    public long getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(long timeLineId) {
        this.timelineId = timeLineId;
    }

    @JSON
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @JSON(classType = JSON.CLASS_TYPE_ARRAY, clazz = Series.class)
    public ArrayList<Series> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<Series> series) {
        this.series = series;
    }

    @JSON
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @JSON
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
