package ir.markazandroid.masteradvertiser.object;

import java.io.Serializable;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EFile)) return false;

        EFile eFile = (EFile) o;

        if (lastModified != eFile.lastModified) return false;
        if (url != null ? !url.equals(eFile.url) : eFile.url != null) return false;
        return eFileId.equals(eFile.eFileId);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + eFileId.hashCode();
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        return result;
    }
}
