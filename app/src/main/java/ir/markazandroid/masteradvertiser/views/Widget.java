package ir.markazandroid.masteradvertiser.views;

import org.json.JSONObject;

/**
 * Coded by Ali on 6/22/2019.
 */
public interface Widget {
    void init(JSONObject extras);

    /**
     *
     * @return true if campaignService should wait until this widget is done before ending current campaign
     */
    default boolean shouldWaitUntilDone(){
        return false;
    }

    default void setWidgetIsDoneListener(WidgetIsDoneListener listener){
    }

    void dispose();


    interface WidgetIsDoneListener{
        void broadcastThisWidgetIsDone(Widget widget);
    }
}
