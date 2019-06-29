package ir.markazandroid.masteradvertiser.object;

import java.io.Serializable;

import ir.markazandroid.masteradvertiser.downloader.UniqueIdentifier;
import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Created by Ali on 4/15/2019.
 */
@JSON
public class EFile implements Serializable{
    private String url;
    private String eFileId;
    private long lastModified;

    @JSON
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JSON
    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @JSON
    public String geteFileId() {
        return eFileId;
    }

    public void seteFileId(String eFileId) {
        this.eFileId = eFileId;
    }
}
