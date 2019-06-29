package ir.markazandroid.masteradvertiser.views;

import org.json.JSONObject;

/**
 * Coded by Ali on 6/22/2019.
 */
public interface Widget {
    void init(JSONObject extras);
    void dispose();
}
