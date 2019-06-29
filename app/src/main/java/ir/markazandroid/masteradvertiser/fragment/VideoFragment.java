package ir.markazandroid.masteradvertiser.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import ir.markazandroid.masteradvertiser.R;
import ir.markazandroid.masteradvertiser.downloader.MasterDownloader;
import ir.markazandroid.masteradvertiser.object.EFile;
import ir.markazandroid.masteradvertiser.views.playlist.data.Video;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends SelectiveFragment implements MasterDownloader.EFileFetchListener {

    private VideoStateChangeListener videoStateChangeListener;
    private Video video;
    private SimpleExoPlayer player;
    private File file;
    private int repeatMode;
    private int videoCount = 0;
    private boolean started;


    private void prepare() {
        if (videoStateChangeListener == null)
            videoStateChangeListener = (VideoStateChangeListener) getActivity();

        repeatMode = getArguments().getInt(REPEAT_MODE, Player.REPEAT_MODE_OFF);

        playerView.setPlayer(player);

        MediaSource mediaSource = newVideoSource(file);

        //player.setRepeatMode(repeatMode);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        player.prepare(mediaSource);

        player.addListener(new Player.DefaultEventListener() {
            /*@Override
            public void onLoadingChanged(boolean isLoading) {
                if (!isLoading)
                    player.setPlayWhenReady(true);
            }*/

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e("player", playbackState + "");
                if (playbackState == Player.STATE_IDLE && !playWhenReady) {
                    //           videoCount++;
                    videoStateChangeListener.onVideoFinish(VideoFragment.this);
                } else if (playbackState == Player.STATE_ENDED)
                    videoStateChangeListener.onVideoFinish(VideoFragment.this);
                //     if (videoCount==videoUrl.size()) {
                //         videoStateChangeListener.onVideoFinish();
                //        videoCount=0;
                //   }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("error", "error");
                error.printStackTrace();
                player.setPlayWhenReady(false);
            }
        });
    }

    private void play() {
        player.seekTo(C.TIME_UNSET);
        player.setPlayWhenReady(true);
        Log.e("cycle", "init");
    }

    private void prepareAndPlay() {
        prepare();
        play();
    }


    @Override
    protected void start() {
        started = true;
        if (file == null) {
            getDownloader().giveMeFile(video.geteFile(), this);
        } else
            prepareAndPlay();
    }

    @Override
    protected void stop() {
        started = false;
        Log.e("cycle", "stop");
    }

    @Override
    protected void refresh() {
        //    stop();
        //   start();
    }

    @Override
    public void onEFileStatusChange(EFile eFile, int status, long downloaded, long total) {
        switch (status) {
            case MasterDownloader.EFILE_STATUS_CANCELED:
            case MasterDownloader.EFILE_STATUS_FAILED:
            case MasterDownloader.EFILE_STATUS_FAILED_NO_RETRY:
                if (file == null)
                    videoStateChangeListener.onVideoFinish(this);
        }
    }

    @Override
    public void onFileReadyFromCache(File file) {
        this.file = file;
        prepare();
        if (started)
            play();
    }

    @Override
    public void onFileReadyFromNet(File file, boolean cached) {
        if (this.file == null || !cached) {
            this.file = file;
            prepare();
            if (started)
                play();
        }
    }

    public interface VideoStateChangeListener {
        void onVideoFinish(Fragment frag);
    }


    private final String VIDEO_URL = "video_url";
    private final String REPEAT_MODE = "repeat_mode";


    public VideoFragment() {
        Bundle bundle = new Bundle();
        setArguments(bundle);
    }

    public VideoFragment videoUrl(Video video) {
        getArguments().putSerializable(VIDEO_URL, video);
        return this;
    }

    public VideoFragment repeatMode(int repeatMode) {
        getArguments().putInt(REPEAT_MODE, repeatMode);
        return this;
    }

    public VideoFragment videoStateChangeListener(VideoStateChangeListener listener) {
        videoStateChangeListener = listener;
        return this;
    }

    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
    }

    private MediaSource newVideoSource(File url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        String userAgent = Util.getUserAgent(getActivity(), "Advertiser");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.fromFile(url), dataSourceFactory, extractorsFactory, null, null);
    }

    private SimpleExoPlayerView playerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        playerView = view.findViewById(R.id.player);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (playerView != null && playerView.getPlayer() != null)
            playerView.getPlayer().release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDownloader().removeListener(video.geteFile(), this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        player = newSimpleExoPlayer();
        video = (Video) getArguments().getSerializable(VIDEO_URL);
    }
}
