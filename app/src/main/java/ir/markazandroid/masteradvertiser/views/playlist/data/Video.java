package ir.markazandroid.masteradvertiser.views.playlist.data;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Coded by Ali on 6/22/2019.
 */
@JSON
public class Video extends DataEntity {
    public static final String DATA_TYPE_VIDEO = "video";

    public Video() {
        setDataType(DATA_TYPE_VIDEO);
    }
}
