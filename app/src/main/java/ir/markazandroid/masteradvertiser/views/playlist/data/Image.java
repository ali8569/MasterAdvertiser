package ir.markazandroid.masteradvertiser.views.playlist.data;

import java.io.Serializable;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Coded by Ali on 6/22/2019.
 */
@JSON
public class Image extends DataEntity implements Serializable {
    public static final String DATA_TYPE_IMAGE = "image";

    private String scaleType;

    public Image() {
        setDataType(DATA_TYPE_IMAGE);
    }

    @JSON
    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }
}
