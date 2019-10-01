package ir.markazandroid.masteradvertiser.object;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;
import ir.markazandroid.masteradvertiser.util.date.DateUtils;
import java8.util.function.ToLongFunction;
import java8.util.stream.StreamSupport;

/**
 * Created by Ali on 8/31/2019.
 */
public class Series implements Serializable {

    public static final String DELIMITER_KEEP = "keep";
    public static final String DELIMITER_START_OVER = "start_over";
    public static final String DELIMITER_END = "end";
    public static final int WE_ARE_IN_IT=1;
    public static final int ENDED_IN_THE_PAST=2;
    public static final int NOT_STARTED_YET=3;

    private ArrayList<SeriesCampaign> campaigns;
    private String delimiter;
    private long from;
    private long duration;

    @JSON(classType = JSON.CLASS_TYPE_ARRAY, clazz = SeriesCampaign.class)
    public ArrayList<SeriesCampaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(ArrayList<SeriesCampaign> campaigns) {
        this.campaigns = campaigns;
    }

    @JSON
    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @JSON
    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }


    /**
     *
     * @return series duration in seconds
     */
    public long getDuration() {
        if (duration==0){
            duration=StreamSupport.stream(campaigns)
                    .mapToLong(SeriesCampaign::getShowingDuration)
                    .sum();
        }
        return duration;
    }

    public int getState() {
        Date seriesFrom =getSeriesFrom();
        Date seriesEnd =new Date(seriesFrom.getTime() + getDuration()*1000);
        Date now = new Date();

        if (now.equals(seriesFrom) || (seriesFrom.before(now) && seriesEnd.after(now)))
            return Series.WE_ARE_IN_IT;
        else if (seriesFrom.after(now))
            return Series.NOT_STARTED_YET;
        else
            return Series.ENDED_IN_THE_PAST;
    }

    public Date getSeriesFrom(){
        return new Date(DateUtils.getTodayDate().getTime() + getFrom()*1000);
    }
}
