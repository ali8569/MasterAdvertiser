package ir.markazandroid.masteradvertiser.views.playlist.data;

import java.io.Serializable;
import java.util.ArrayList;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Coded by Ali on 6/22/2019.
 */
@JSON
public class Data implements Serializable {
    private ArrayList<DataEntity> entities;
    private int duration;

    @JSON(classType = JSON.CLASS_TYPE_ARRAY, clazz = DataEntity.class
            , classTypes = @JSON.ClassType(parameterName = "dataType"
            , clazzes = {@JSON.Clazz(name = Image.DATA_TYPE_IMAGE, clazz = Image.class),
            @JSON.Clazz(name = Video.DATA_TYPE_VIDEO, clazz = Video.class)}))
    public ArrayList<DataEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<DataEntity> entities) {
        this.entities = entities;
    }

    @JSON
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
