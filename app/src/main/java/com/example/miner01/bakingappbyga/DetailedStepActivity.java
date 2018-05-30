package com.example.miner01.bakingappbyga;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_DESCRIPTION;
import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_STEP_NUMBER;
import static com.example.miner01.bakingappbyga.StepsFragment.EXTRA_VIDEOURL;

public class DetailedStepActivity extends AppCompatActivity implements ExoPlayer.EventListener {


    public static final String TAG = DetailedStepActivity.class.getSimpleName();
    private static MediaSessionCompat mMediaSession;
    private View view;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;
    private ArrayList<String[]> currentRecipeDetailsWithStepNo1 = new ArrayList<>();
    private String recipeNumber;
    private String stepNumber;
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
    private int maxNumberOfSteps;
    private ImageView mNoVideoAvailabe;
    private Uri uriCurrentVideoStep;
    private Uri mCurrentItemUri;
    private ConstraintLayout.LayoutParams layoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_step);

        currentRecipeDetailsWithStepNo1 = StepsFragment.currentRecipeDetailsWithStepNo;
        maxNumberOfSteps = currentRecipeDetailsWithStepNo1.size();
        Log.i("max number of steps", String.valueOf(maxNumberOfSteps));


        // Initialize the player view.
        mPlayerView = findViewById(R.id.playerView);
        mNoVideoAvailabe = findViewById(R.id.noVideoAvailable);
        mNoVideoAvailabe.setVisibility(View.GONE);
        Context context = mNoVideoAvailabe.getContext();
        Picasso.with(context).load(R.drawable.no_video).into(mNoVideoAvailabe);

        mDetailedDescription = findViewById(R.id.detailed_description);
        mCurrentRecipeNoLabel = getResources().getString(R.string.current_step);
        mCurrentRecipeNo = findViewById(R.id.step_number);
        mStepForth = findViewById(R.id.step_forth);
        mStepBack = findViewById(R.id.step_back);

        String currentStepNumber = "";
        String currentDetailedDescription = "";
        String currentVideoStep = "";

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDetailedDescription.setText(currentDetailedDescription);
            mPlayerView.setVisibility(View.VISIBLE);
            mDetailedDescription.setVisibility(View.VISIBLE);
            mCurrentRecipeNo.setVisibility(View.VISIBLE);
            mStepForth.setVisibility(View.VISIBLE);
            mStepBack.setVisibility(View.VISIBLE);

        } else {


//            DisplayMetrics displayMetrics = new DisplayMetrics();
//
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//            int width = displayMetrics.widthPixels;
//            int height = displayMetrics.heightPixels;
//
//
//            layoutParams = new ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.MATCH_PARENT,
//                    ConstraintLayout.LayoutParams.MATCH_PARENT);


            if (uriCurrentVideoStep == null) {
                mPlayerView.setVisibility(View.GONE);

                mNoVideoAvailabe.setVisibility(View.VISIBLE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    mNoVideoAvailabe.setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                }
//
//                mNoVideoAvailabe.setLayoutParams(layoutParams);


            } else {
                mNoVideoAvailabe.setVisibility(View.GONE);

                mPlayerView.setVisibility(View.VISIBLE);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    mPlayerView.setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                }
//
//                mPlayerView.setLayoutParams(layoutParams);

            }


//            mDetailedDescription.setVisibility(View.GONE);
//            mCurrentRecipeNo.setVisibility(View.GONE);
//            mStepForth.setVisibility(View.GONE);
//            mStepBack.setVisibility(View.GONE);
        }


        initializeMediaSession();

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();

        currentStepNumber = intent.getStringExtra(EXTRA_STEP_NUMBER);
        currentStepNumberInt = Integer.parseInt(intent.getStringExtra(EXTRA_STEP_NUMBER));

        currentDetailedDescription = intent.getStringExtra(EXTRA_DESCRIPTION);
        Log.i("detailed_step_descr", currentDetailedDescription);

        currentVideoStep = intent.getStringExtra(EXTRA_VIDEOURL);
        Log.i("detailed_step_link", currentVideoStep);

        uriCurrentVideoStep = checkUrl(currentVideoStep);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDetailedDescription.setText(currentDetailedDescription);

            if (currentStepNumberInt == (maxNumberOfSteps - 1)) {
                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        "Last Step"));
                mStepForth.setVisibility(View.GONE);
            } else if (currentStepNumberInt == 0) {
                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        "Introduction"));
                mStepBack.setVisibility(View.GONE);
            } else {
                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        currentStepNumber));
            }
        }
        loadVideo(uriCurrentVideoStep);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mStepForth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int nextStepNo = getNextStepNo(currentStepNumberInt);

                    String[] nextStep = getNextPrevStep(nextStepNo);
                    int tempInt = Integer.parseInt(nextStep[1]);

                    if (tempInt == (maxNumberOfSteps - 1)) {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                "Last Step"));
                        mStepForth.setVisibility(View.GONE);
                    } else {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                nextStep[1]));
                    }
                    mDetailedDescription.setText(nextStep[3]);
                    Uri tempUrl = checkUrl(nextStep[4]);
                    loadVideo(tempUrl);
                }
            });


            mStepBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int backStepNo = getPrevStepNo(currentStepNumberInt);

                    String[] backStep = getNextPrevStep(backStepNo);
                    int tempInt = Integer.parseInt(backStep[1]);

                    if (tempInt == 0) {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                "Introduction"));
                        mStepBack.setVisibility(View.GONE);
                    } else {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                backStep[1]));
                    }
                    mDetailedDescription.setText(backStep[3]);
                    Uri tempUrl = checkUrl(backStep[4]);
                    loadVideo(tempUrl);
                }
            });
        }

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
        mMediaSession = new MediaSessionCompat(this, TAG);

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
        mMediaSession.setCallback(new DetailedStepActivity.MySessionCallback());

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
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private Uri checkUrl(String stringUrl) {

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

        return uriCurrentVideoStep;
    }


    private void loadVideo(Uri uri) {
        releasePlayer();
        initializeMediaSession();


        if (uri == null) {
//            Toast.makeText(getActivity(), getString(R.string.sample_not_found_error),
//                    Toast.LENGTH_SHORT).show();
//            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
//                    (getResources(), R.drawable.no_video));
            mPlayerView.setVisibility(View.GONE);
            mNoVideoAvailabe.setVisibility(View.VISIBLE);


        } else {
            mPlayerView.setVisibility(View.VISIBLE);
            mNoVideoAvailabe.setVisibility(View.GONE);
            // Initialize the player.
            initializePlayer(uri);
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

    private int getNextStepNo(int currentStepNo) {

        if (currentStepNo == (maxNumberOfSteps - 1)) {
            currentStepNumberInt = currentStepNo;
            mStepForth.setVisibility(View.GONE);

        } else {
            currentStepNumberInt = currentStepNo + 1;
            mStepForth.setVisibility(View.VISIBLE);
            mStepBack.setVisibility(View.VISIBLE);
        }

        return currentStepNumberInt;
    }

    private int getPrevStepNo(int currentStepNo) {

        if (currentStepNo == 0) {
            currentStepNumberInt = currentStepNo;
            mStepBack.setVisibility(View.GONE);

        } else {
            currentStepNumberInt = currentStepNo - 1;
            mStepBack.setVisibility(View.VISIBLE);
            mStepForth.setVisibility(View.VISIBLE);
        }
        return currentStepNumberInt;
    }

    private String[] getNextPrevStep(int prevNextStepNo) {
        String[] prevNextStep = new String[5];

        ArrayList<String[]> tempCurrentSteps = currentRecipeDetailsWithStepNo1;
        for (int i = 0; i < tempCurrentSteps.size(); i++) {
            String[] elements = tempCurrentSteps.get(i);
            int stepNo = Integer.parseInt(elements[1]);

            if (stepNo == prevNextStepNo) {
                prevNextStep[0] = elements[0];
                prevNextStep[1] = elements[1];
                prevNextStep[2] = elements[2];
                prevNextStep[3] = elements[3];
                prevNextStep[4] = elements[4];
            }
        }
        return prevNextStep;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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


}
