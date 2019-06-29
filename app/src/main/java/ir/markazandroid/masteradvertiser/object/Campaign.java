package ir.markazandroid.masteradvertiser.object;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Coded by Ali on 6/22/2019.
 */
public class Campaign implements Serializable {

    private long campaignId;
    private AndroidData androidData;

    @JSON(classType = JSON.CLASS_TYPE_OBJECT,clazz = AndroidData.class)
    public AndroidData getAndroidData() {
        return androidData;
    }

    public void setAndroidData(AndroidData androidData) {
        this.androidData = androidData;
    }

    @JSON
    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }


    public static class AndroidData implements Serializable {
        private String xml;
        private JSONObject extras;

        public AndroidData(String xml, JSONObject extras) {
            this.xml = xml;
            this.extras = extras;
        }

        public AndroidData() {
        }

        @JSON
        public JSONObject getExtras() {
            return extras;
        }

        public void setExtras(JSONObject extras) {
            this.extras = extras;
        }

        @JSON
        public String getXml() {
            return xml;
        }

        public void setXml(String xml) {
            this.xml = xml;
        }
    }
}
