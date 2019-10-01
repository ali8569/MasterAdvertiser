package ir.markazandroid.masteradvertiser.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.network.JSONParser.Parser;
import ir.markazandroid.masteradvertiser.object.Campaign;
import ir.markazandroid.masteradvertiser.object.ErrorObject;
import ir.markazandroid.masteradvertiser.object.Phone;
import ir.markazandroid.masteradvertiser.object.Schedule;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    public void getMe(OnResultLoaded.ActionListener<Phone> actionListener) {
        Request request = new Request.Builder()
                .url(NetStatics.DEVICE_GET_ME)
                .get()
                .build();

       /* client.newCall(request).enqueue(new CBack() {
        });*/
    }

    @Override
    public void getSchedule(long update, OnResultLoaded.AvailableListener<Schedule> result) {
        Request request = new Request.Builder()
                .url(String.format(Locale.US,"%s?update=%d",NetStatics.DEVICE_SCHEDULE_GET,update))
                .get()
                .build();
        client.newCall(request).enqueue(new CBack(context, tag) {

            @Override
            public boolean isSuccessfull(int code) {
                if (code==304) {
                    handler.post(result::onNotAvailable);
                    return false;
                }
                return super.isSuccessfull(code);
            }

            @Override
            public void result(JSONObject response) {
                Schedule schedule = parser.get(Schedule.class, response);
                schedule.setLastUpdate(Long.parseLong(getHeaders().get("lastUpdate")));
                handler.post(() -> result.onAvailable(schedule));
            }

            @Override
            public void fail(IOException e) {
                handler.post(() -> result.failed(e));
            }

        });
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


    @Override
    public void login(String uuid, final OnResultLoaded.ActionListener<Phone> actionListener) {
        Request request = new Request.Builder()
                .url(NetStatics.REGISTRATION_LOGIN)
                .addHeader("Accept", "application/json")
                .post(new FormBody.Builder().add("uuid",uuid).build())
                .build();

        client.newCall(request).enqueue(new CBack(context,tag) {
            @Override
            public boolean isSuccessfull(int code) {
                return true;
            }

            @Override
            public boolean isSuccessfull(int code, JSONObject response) {
                if (code==200) actionListener.onSuccess(parser.get(Phone.class,response));
                else actionListener.onError(parser.get(ErrorObject.class,response));
                return false;
            }

            @Override
            public void fail(IOException e) {
                actionListener.failed(e);
            }
        });
    }


    @Override
    public Campaign getCampaign(long campaignId) throws IOException {
        Request request = new Request.Builder()
                .url(String.format(Locale.US,"%s?campaignId=%d",NetStatics.DEVICE_CAMPAIGN_GET,campaignId))
                .get()
                .build();
        try(Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return parser.get(Campaign.class, new JSONObject(response.body().string()));
            }
            else {
                Log.e("Error", response.body().string());
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


}
