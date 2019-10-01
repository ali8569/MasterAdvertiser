package ir.markazandroid.masteradvertiser.object;

import java.io.Serializable;
import java.util.ArrayList;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;


/**
 * Created by Ali on 4/8/2019.
 */
public class Schedule implements Serializable {
    private int scheduleId;
    private int type;
    private Long userId;
    private ArrayList<SHT> scheduleHasTimeLines;
    private String name;
    private long createTime;

    //from header
    private long lastUpdate;

    @JSON
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    @JSON
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JSON
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    @JSON(classType = JSON.CLASS_TYPE_ARRAY, clazz = SHT.class)
    public ArrayList<SHT> getScheduleHasTimeLines() {
        return scheduleHasTimeLines;
    }

    public void setScheduleHasTimeLines(ArrayList<SHT> scheduleHasTimeLines) {
        this.scheduleHasTimeLines = scheduleHasTimeLines;
    }

    @JSON
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSON
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @JSON
    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
