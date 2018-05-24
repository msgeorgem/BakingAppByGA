package com.example.miner01.bakingappbyga;


import android.app.Fragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_DESCRIPTION;
import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_VIDEOURL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailedStepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailedStepFragment} factory method to
 * create an instance of this fragment.
 */

public class DetailedStepFragment extends Fragment implements ExoPlayer.EventListener {


    public static final String TAG = DetailedStepFragment.class.getSimpleName();
    private View view;
    private static MediaSessionCompat mMediaSession;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    public DetailedStepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detailed_step, container, false);

        String currentDetailedDescription = "";

        String currentVideoStep = "";

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentDetailedDescription = bundle.getString(EXTRA_DESCRIPTION);
            Log.i("detailed_step_descr", currentDetailedDescription);
            currentVideoStep = bundle.getString(EXTRA_VIDEOURL);
            Log.i("detailed_step_link", currentVideoStep);
        }
        TextView detailedDescription = view.findViewById(R.id.detailed_description);
        detailedDescription.setText(currentDetailedDescription);


        // Initialize the player view.
        mPlayerView = view.findViewById(R.id.playerView);
        // Load the question mark as the background image until the user answers the question.
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.no_video));

        // String currentVideoUrl = intent.getStringExtra(StepsFragment.EXTRA_VIDEOURL);
        // Initialize the Media Session.
        initializeMediaSession();

        URL url;
        Uri uriCurrentVideoStep = null;
        try {
            url = new URL(currentVideoStep);
            uriCurrentVideoStep = Uri.parse(url.toURI().toString());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (uriCurrentVideoStep == null) {
            Toast.makeText(getActivity(), getString(R.string.sample_not_found_error),
                    Toast.LENGTH_SHORT).show();
            return view;
        } else {
            // Initialize the player.
            initializePlayer(uriCurrentVideoStep);
            return view;
        }
    }

    /**
     * Shows Media Style notification, with actions that depend on the current MediaSession
     * PlaybackState.
     * @param state The PlaybackState of the MediaSession.
     */
//    private void showNotification(PlaybackStateCompat state) {
//        Notification.Builder builder = new Notification.Builder(getActivity());
//
//        int icon;
//        String play_pause;
//        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
//            icon = R.drawable.exo_controls_pause;
//            play_pause = getString(R.string.pause);
//        } else {
//            icon = R.drawable.exo_controls_play;
//            play_pause = getString(R.string.play);
//        }
//
//
//        Action playPauseAction = new Action(
//                icon, play_pause,
//                MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
//                        PlaybackStateCompat.ACTION_PLAY_PAUSE));
//
//        Action restartAction = newAction(R.drawable.exo_controls_previous, getString(R.string.restart),
//                MediaButtonReceiver.buildMediaButtonPendingIntent
//                        (getActivity(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
//
//        PendingIntent contentPendingIntent = PendingIntent.getActivity
//                (getActivity(), 0, new Intent(getActivity(), DetailedStepFragment.class), 0);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder.setContentTitle(getString(R.string.guess))
//                        .setContentText(getString(R.string.notification_text))
//                        .setContentIntent(contentPendingIntent)
//                        .setSmallIcon(R.drawable.ic_music_note)
//                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                        .addAction(restartAction)
//                        .addAction(playPauseAction)
//                        .setStyle(new MediaStyle()
//                                .setMediaSession(mMediaSession.getSessionToken())
//                                .setShowActionsInCompactView(0,1));
//            }
//        }
//
//
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, builder.build());
//    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
//        mNotificationManager.cancelAll();
        if ((mExoPlayer != null)) {
            mExoPlayer.stop();
        }
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
//        showNotification(mStateBuilder.build());

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
