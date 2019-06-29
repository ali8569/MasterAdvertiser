package ir.markazandroid.masteradvertiser.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.R;
import ir.markazandroid.masteradvertiser.network.JSONParser.Parser;
import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;
import ir.markazandroid.masteradvertiser.signal.Signal;
import ir.markazandroid.masteradvertiser.signal.SignalReceiver;
import ir.markazandroid.masteradvertiser.util.Utils;

/**
 * Coded by Ali on 6/25/2019.
 */
public class WebPageView extends FrameLayout implements Widget, SignalReceiver {

    private WebView webView;
    private Button go;
    private ImageView webviewBack, webviewForward, webviewHome;
    private EditText addressBar;
    private ProgressBar webviewProgressBar;
    private String homeUrl;
    private boolean shouldRefreshOnNet,weAreOnHome;
    private String currentUrl;
    private Parser parser;
    private WebViewExtras extras;
    private Timer goBackToHomeTimer;
    private Handler handler;

    public WebPageView(@NonNull Context context) {
        super(context);
        View innerView = LayoutInflater.from(context).inflate(R.layout.widget_web_page_view,this,false);
        addView(innerView);

        parser=((MasterAdvertiserApplication)context.getApplicationContext()).getParser();

        handler=new Handler(context.getMainLooper());

        webView = findViewById(R.id.webView);
        addressBar = findViewById(R.id.url);
        go = findViewById(R.id.go);
        webviewBack = findViewById(R.id.webViewBack);
        webviewForward = findViewById(R.id.webViewForward);
        webviewHome = findViewById(R.id.webViewHome);
        webviewProgressBar = findViewById(R.id.webViewProgressBar);
        ((MasterAdvertiserApplication)context.getApplicationContext()).getSignalManager().addReceiver(this);

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                currentUrl=url;
                webLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webLoading(false);
                shouldRefreshOnNet=false;
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
                switch (errorCode){
                    case  WebViewClient.ERROR_CONNECT:
                    case  WebViewClient.ERROR_FAILED_SSL_HANDSHAKE:
                    case  WebViewClient.ERROR_HOST_LOOKUP:
                    case  WebViewClient.ERROR_IO:
                    case  WebViewClient.ERROR_TIMEOUT:
                    case  WebViewClient.ERROR_UNKNOWN:
                        shouldRefreshOnNet=true;
                }

            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                if (req.isForMainFrame())
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        go.setOnClickListener(v -> {
            String u = addressBar.getText().toString();
            goToPage(u);
        });

        webviewBack.setOnClickListener(v -> {
            if (webView.canGoBack())
                webView.goBack();

        });
        webviewForward.setOnClickListener(v -> {
            if (webView.canGoForward())
                webView.goForward();
        });
        webviewHome.setOnClickListener(v -> {
            gotoHomePage();
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (goBackToHomeTimer!=null)
                    goBackToHomeTimer.cancel();
                goBackToHomeTimer=new Timer();
                goBackToHomeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //if (!weAreOnHome)
                        handler.post(WebPageView.this::gotoHomePage);
                    }
                },1*60*1000);

                Log.d(getClass().getSimpleName(),"Touch");
                return false;
            }
        });
    }

    private void webLoading(boolean isLoading) {
        if (isLoading) {
            Utils.fade(webviewProgressBar, webviewHome, 500);
            Utils.fadeFade(webviewBack, 500, true);
            Utils.fadeFade(webviewForward, 500, true);
        } else {
            Utils.fade(webviewHome, webviewProgressBar, 500);
            Utils.fadeVisible(webviewBack, 500);
            Utils.fadeVisible(webviewForward, 500);
        }
    }

    @Override
    public void init(JSONObject jsonExtras) {
        this.extras=parser.get(WebViewExtras.class,jsonExtras);

        homeUrl=extras.getHomeUrl();

        gotoHomePage();
    }

    private void gotoHomePage() {
        webView.clearHistory();
        goToPage(homeUrl);
        weAreOnHome=true;
    }

    private void goToPage(String url){
        webView.loadUrl(fixUrl(url));
    }

    private void reloadPage(){
        webView.reload();
    }

    @Override
    public void dispose() {
        ((MasterAdvertiserApplication)getContext().getApplicationContext()).getSignalManager().removeReceiver(this);
        if (goBackToHomeTimer!=null)
            goBackToHomeTimer.cancel();
    }

    private String fixUrl(String urlLink) {
        if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://")) {
            urlLink = "http://" + urlLink;
        }
        return urlLink;
    }


    @Override
    public boolean onSignal(Signal signal) {
        if (signal.getType()==Signal.DOWNLOADER_NETWORK && shouldRefreshOnNet)
            reloadPage();

        return false;
    }

    public static class WebViewExtras implements Serializable {
        private String homeUrl;

        @JSON
        public String getHomeUrl() {
            return homeUrl;
        }

        public void setHomeUrl(String homeUrl) {
            this.homeUrl = homeUrl;
        }
    }
}
