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
import java.util.ArrayList;
import java.util.Locale;

import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_DESCRIPTION;
import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_STEP_NUMBER;
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
    private ArrayList<String[]> currentRecipeDetailsWithStepNo1 = new ArrayList<>();
    private int recipeNumber;
    private int stepNumber;
    private String shortDescription;
    private String detailedDescription;
    private String videoStep;
    private int currentStepNumberInt;
    private Bundle bundle;
    private TextView mCurrentRecipeNo;
    private TextView mDetailedDescription;
    private String mCurrentRecipeNoLabel;
    private TextView mStepForth;
    private TextView mStepBack;



    public DetailedStepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detailed_step, container, false);

        currentRecipeDetailsWithStepNo1 = StepsFragment.currentRecipeDetailsWithStepNo;
        // Initialize the player view.
        mPlayerView = view.findViewById(R.id.playerView);
        mDetailedDescription = view.findViewById(R.id.detailed_description);
        mCurrentRecipeNoLabel = getResources().getString(R.string.current_step);
        mCurrentRecipeNo = view.findViewById(R.id.step_number);
        mStepForth = view.findViewById(R.id.step_forth);

        String currentStepNumber = "";
        String currentDetailedDescription = "";
        String currentVideoStep = "";

        initializeMediaSession();

        bundle = this.getArguments();
        if (bundle != null) {
            currentStepNumber = bundle.getString(EXTRA_STEP_NUMBER);
            currentStepNumberInt = Integer.parseInt(bundle.getString(EXTRA_STEP_NUMBER));

            currentDetailedDescription = bundle.getString(EXTRA_DESCRIPTION);
            Log.i("detailed_step_descr", currentDetailedDescription);

            currentVideoStep = bundle.getString(EXTRA_VIDEOURL);
            Log.i("detailed_step_link", currentVideoStep);

            mDetailedDescription.setText(currentDetailedDescription);

            mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                    currentStepNumber));
            loadVideo(currentVideoStep);

        } else {

        }


        mStepForth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nextStepNo = getNextStepNo(currentStepNumberInt);

                String[] nextStep = getNextPrevStep(nextStepNo);

                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        nextStep[1]));

                mDetailedDescription.setText(nextStep[3]);
                loadVideo(nextStep[4]);

            }
        });

        mStepBack = view.findViewById(R.id.step_back);



        // String currentVideoUrl = intent.getStringExtra(StepsFragment.EXTRA_VIDEOURL);
        // Initialize the Media Session.


        return view;
    }


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

    private void loadVideo(String stringUrl) {
        URL url;
        Uri uriCurrentVideoStep = null;
        try {
            url = new URL(stringUrl);
            uriCurrentVideoStep = Uri.parse(url.toURI().toString());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (uriCurrentVideoStep == null) {
//            Toast.makeText(getActivity(), getString(R.string.sample_not_found_error),
//                    Toast.LENGTH_SHORT).show();
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.no_video));

        } else {
            // Initialize the player.
            initializePlayer(uriCurrentVideoStep);
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

    private int getNextStepNo(int currentStepNo) {
        int nextStep;
        nextStep = currentStepNo + 1;
        return nextStep;
    }

    private int getPrevStepNo(int currentStepNo) {
        int prevStep;
        prevStep = currentStepNo + 1;
        return prevStep;
    }

    private String[] getNextPrevStep(int prevNextStepNo) {
        String[] prevNextStep = new String[5];

        ArrayList<String[]> tempCurrentSteps = currentRecipeDetailsWithStepNo1;
        for (int i = 0; i < tempCurrentSteps.size(); i++) {
            String[] elements = tempCurrentSteps.get(i);
            int stepNo = Integer.parseInt(elements[1]);

            if (stepNo == prevNextStepNo) {
                recipeNumber = Integer.parseInt(prevNextStep[0]);
                stepNumber = Integer.parseInt(prevNextStep[1]);
                shortDescription = prevNextStep[2];
                detailedDescription = prevNextStep[3];
                videoStep = prevNextStep[4];
            }
        }

        return prevNextStep;
    }

}
