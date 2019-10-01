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
        this.duration = 5;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataEntity)) return false;

        DataEntity that = (DataEntity) o;

        if (order != that.order) return false;
        if (duration != that.duration) return false;
        if (!eFile.equals(that.eFile)) return false;
        return dataType.equals(that.dataType);
    }

    private int random = Math.round((float) Math.random()*10000);

    @Override
    public int hashCode() {
        return random;
        /*int result = eFile.hashCode();
        result = 31 * result + order;
        result = 31 * result + duration;
        result = 31 * result + dataType.hashCode();
        return result;*/
    }
}
