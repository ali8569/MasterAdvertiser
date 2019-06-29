package ir.markazandroid.masteradvertiser.network;

import android.content.Context;
import android.os.Handler;

import org.json.JSONObject;

import java.io.IOException;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.network.JSONParser.Parser;
import ir.markazandroid.masteradvertiser.object.Campaign;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Coded by Ali on 03/11/2017.
 */

class NetworkMangerImp implements NetworkManager {


    private Context context;
    private Parser parser;
    private OkHttpClient client;
    private String tag;
    private Handler handler;

    NetworkMangerImp(Context context, String tag) {
        this.tag = tag;
        this.context = context;
        parser = ((MasterAdvertiserApplication) context.getApplicationContext()).getParser();
        client = ((MasterAdvertiserApplication) context.getApplicationContext()).getNetworkClient().getClient();
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public void getCampaign(OnResultLoaded<Campaign> result) {
        Request request = new Request.Builder()
                .url(NetStatics.DEVICE_CAMPAIGN_GET)
                .get()
                .build();
        client.newCall(request).enqueue(new CBack(context, tag) {

            @Override
            public void result(JSONObject response) {
                Campaign campaign = parser.get(Campaign.class, response);
                handler.post(() -> result.loaded(campaign));
            }

            @Override
            public void fail(IOException e) {
                handler.post(() -> result.failed(e));
            }

        });

    }


}
