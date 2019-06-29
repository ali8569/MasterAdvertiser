package ir.markazandroid.masteradvertiser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ir.markazandroid.masteradvertiser.media.CampaignService;
import ir.markazandroid.masteradvertiser.network.NetworkManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager networkManager = new NetworkManager.NetworkManagerBuilder()
                .from(this)
                .tag(toString())
                .build();
        CampaignService campaignService = new CampaignService(findViewById(R.id.mainContentHolder),networkManager,getSupportFragmentManager());

        campaignService.start();
    }
}
