package ir.markazandroid.masteradvertiser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ir.markazandroid.masteradvertiser.network.NetworkManager;
import ir.markazandroid.masteradvertiser.network.OnResultLoaded;
import ir.markazandroid.masteradvertiser.object.ErrorObject;
import ir.markazandroid.masteradvertiser.object.Phone;
import ir.markazandroid.masteradvertiser.service.CampaignService;
import ir.markazandroid.masteradvertiser.service.FetcherService;
import ir.markazandroid.masteradvertiser.signal.Signal;
import ir.markazandroid.masteradvertiser.signal.SignalReceiver;

public class MainActivity extends AppCompatActivity implements SignalReceiver {

    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doLogin();

    }

    private void doLogin() {
        getNetworkManager().login("78c317b9-7688-47b7-821a-e595a4faa143", new OnResultLoaded.ActionListener<Phone>() {
            @Override
            public void onSuccess(final Phone successResult) {
                runOnUiThread(() -> {
                    start();
                });
            }

            @Override
            public void onError(final ErrorObject error) {
            }

            @Override
            public void failed(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> doLogin());
            }
        });
    }

    private void start(){
        ((MasterAdvertiserApplication)getApplicationContext()).getSignalManager().addReceiver(this);
        getCampaignService().setShowingProperties(findViewById(R.id.mainContentHolder),getSupportFragmentManager());
        if (!getFetcherService().isStarted())
            getFetcherService().start();
    }

    @Override
    public boolean onSignal(Signal signal) {
        if (signal.getType()==Signal.SIGNAL_ACTIVITY_RECREATE)
            setContentView(R.layout.activity_main);
        else if (signal.getType()==Signal.SIGNAL_LOGOUT)
            doLogin();

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Destroyed","dis");
        ((MasterAdvertiserApplication)getApplicationContext()).getSignalManager().removeReceiver(this);
        getCampaignService().stop();
    }

    private CampaignService getCampaignService(){
        return ((MasterAdvertiserApplication)getApplicationContext()).getCampaignService();
    }
    private FetcherService getFetcherService(){
        return ((MasterAdvertiserApplication)getApplicationContext()).getFetcherService();
    }

    protected NetworkManager getNetworkManager() {
        if (networkManager==null){
            networkManager= new NetworkManager.NetworkManagerBuilder()
                    .from(this)
                    .tag(toString())
                    .build();
        }
        return networkManager;
    }

}
