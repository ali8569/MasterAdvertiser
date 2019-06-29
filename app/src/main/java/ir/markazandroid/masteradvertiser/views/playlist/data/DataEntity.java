package ir.markazandroid.masteradvertiser.views.playlist.data;

import java.io.File;
import java.io.Serializable;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;
import ir.markazandroid.masteradvertiser.object.EFile;

/**
 * Coded by Ali on 6/22/2019.
 */
@JSON
public class DataEntity implements Serializable {
    private EFile eFile;
    private int order;
    private int duration;
    private String dataType;

    private File file;

    @JSON(classType = JSON.CLASS_TYPE_OBJECT, clazz = EFile.class)
    public EFile geteFile() {
        return eFile;
    }

    public void seteFile(EFile eFile) {
        this.eFile = eFile;
    }

    @JSON
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @JSON
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @JSON
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
