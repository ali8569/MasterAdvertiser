package ir.markazandroid.masteradvertiser.network;

import android.content.Context;

import ir.markazandroid.masteradvertiser.object.Campaign;

/**
 * Coded by Ali on 03/11/2017.
 */

public interface NetworkManager {

    void getCampaign(OnResultLoaded<Campaign> result);



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
