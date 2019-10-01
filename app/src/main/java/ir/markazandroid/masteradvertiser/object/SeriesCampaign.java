package ir.markazandroid.masteradvertiser.object;

import java.io.Serializable;
import java.util.Date;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;

/**
 * Created by Ali on 8/31/2019.
 */
@JSON
public class SeriesCampaign implements Serializable {

    private long campaignId;
    private Campaign campaign;
    private int duration;

    @JSON
    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    @JSON
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @JSON(classType = JSON.CLASS_TYPE_OBJECT,clazz = Campaign.class)
    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    /**
     *
     * @return showing duration in seconds
     */
    public long getShowingDuration(){
        if (duration==0 && campaign!=null)
            return campaign.getDuration();
        else
            return duration;
    }

    public boolean isUserTimed(){
        return getDuration()!=0;
    }

    public Date getEndCampaign(Date from){
        return new Date(from.getTime()+getShowingDuration() * 1000);
    }


}
