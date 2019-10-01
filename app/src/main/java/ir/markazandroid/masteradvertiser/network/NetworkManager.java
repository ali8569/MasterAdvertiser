package ir.markazandroid.masteradvertiser.network;

import android.content.Context;

import java.io.IOException;

import ir.markazandroid.masteradvertiser.object.Campaign;
import ir.markazandroid.masteradvertiser.object.Phone;
import ir.markazandroid.masteradvertiser.object.Schedule;

/**
 * Coded by Ali on 03/11/2017.
 */

public interface NetworkManager {

    void getMe(OnResultLoaded.ActionListener<Phone> actionListener);

    void getSchedule(long update,OnResultLoaded.AvailableListener<Schedule> result);

    void getCampaign(OnResultLoaded<Campaign> result);

    Campaign getCampaign(long campaignId) throws IOException;


    void login(String uuid, OnResultLoaded.ActionListener<Phone> actionListener);



    class NetworkManagerBuilder {
        private String tag;
        private Context context;

        public NetworkManagerBuilder() {
        }

        public NetworkManagerBuilder from(Context context) {
            this.context = context;
            return this;
        }

        public NetworkManagerBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public NetworkManager build() {
            return new NetworkMangerImp(context, tag);
        }
    }

}
