package com.example.miner01.bakingappbyga;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
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

import xyz.neocrux.suziloader.SuziLoader;

import static com.example.miner01.bakingappbyga.MainActivity.EXTRA_DESCRIPTION;
import static com.example.miner01.bakingappbyga.MainActivity.EXTRA_STEP_NUMBER;
import static com.example.miner01.bakingappbyga.MainActivity.EXTRA_VIDEOURL;


public class DetailedStepActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    public static final String TAG = DetailedStepActivity.class.getSimpleName();
    private static MediaSessionCompat mMediaSession;
    private static SimpleExoPlayer mExoPlayer;
    private static SimpleExoPlayerView mPlayerView;
    private static PlaybackStateCompat.Builder mStateBuilder;
    private ArrayList<String[]> currentRecipeDetailsWithStepNo1 = new ArrayList<>();
    private int currentStepNumberInt;
    private String currentDetailedDescription;
    private TextView mCurrentRecipeNo;
    private TextView mDetailedDescription;
    private String mCurrentRecipeNoLabel;
    private TextView mStepForth;
    private TextView mStepBack;
    private FrameLayout mStepBackFrame;
    private FrameLayout mStepForthFrame;
    private FrameLayout mPlayerViewFrame;
    private int maxNumberOfSteps;
    private TextView mNoVideoAvailabe;
    private ImageView mNoVideoAvailabeImage;
    private Uri uriCurrentVideoStep;
    private Context context;
    private long playerPosition;
    private boolean isPlayWhenReady;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_step);
        context = getApplicationContext();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        currentRecipeDetailsWithStepNo1 = StepsFragment.currentRecipeDetailsWithStepNo;
        maxNumberOfSteps = currentRecipeDetailsWithStepNo1.size();
        Log.i("max number of steps", String.valueOf(maxNumberOfSteps));

        // Initialize the player view.
        mPlayerView = findViewById(R.id.playerView);
        mPlayerViewFrame = findViewById(R.id.playerViewFrame);
        mNoVideoAvailabe = findViewById(R.id.noVideoAvailable);
        mNoVideoAvailabeImage = findViewById(R.id.noVideoAvailableImage);
        mNoVideoAvailabeImage.setVisibility(View.GONE);

        mDetailedDescription = findViewById(R.id.detailed_description);
        mCurrentRecipeNoLabel = getResources().getString(R.string.current_step);
        mCurrentRecipeNo = findViewById(R.id.step_number);
        mStepForth = findViewById(R.id.step_forth);
        mStepBack = findViewById(R.id.step_back);
        mStepBackFrame = findViewById(R.id.step_back_frame);
        mStepForthFrame = findViewById(R.id.step_forth_frame);
        playerPosition = C.TIME_UNSET;

        currentDetailedDescription = "";
        String currentVideoStep = "";

        releasePlayer();
        initializeMediaSession();

        Intent intent = getIntent();

        if (savedInstanceState == null) {
            currentVideoStep = intent.getStringExtra(EXTRA_VIDEOURL);
            Log.i("detailed_step_link", currentVideoStep);
            uriCurrentVideoStep = checkUrl(currentVideoStep);
            currentDetailedDescription = intent.getStringExtra(EXTRA_DESCRIPTION);
            Log.i("detailed_step_descr", currentDetailedDescription);
            currentStepNumberInt = intent.getIntExtra(EXTRA_STEP_NUMBER, 0);
        } else {
            uriCurrentVideoStep = checkUrl(savedInstanceState.getString("video"));
            currentStepNumberInt = savedInstanceState.getInt("number");
            currentDetailedDescription = savedInstanceState.getString("description");
            playerPosition = savedInstanceState.getLong("player_position", C.TIME_UNSET);
            isPlayWhenReady = savedInstanceState.getBoolean("playstate");
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDetailedDescription.setText(currentDetailedDescription);
            mPlayerView.setVisibility(View.VISIBLE);
            mDetailedDescription.setVisibility(View.VISIBLE);
            mCurrentRecipeNo.setVisibility(View.VISIBLE);
            mStepForth.setVisibility(View.VISIBLE);
            mStepBack.setVisibility(View.VISIBLE);

        } else {

            if (uriCurrentVideoStep == null) {
                mPlayerView.setVisibility(View.GONE);
                mPlayerViewFrame.setVisibility(View.GONE);
                mNoVideoAvailabe.setVisibility(View.VISIBLE);
                mNoVideoAvailabe.setText(getResources().getString(R.string.no_video_available));

            } else {
                mNoVideoAvailabeImage.setVisibility(View.GONE);
                mNoVideoAvailabe.setVisibility(View.GONE);
                mPlayerView.setVisibility(View.VISIBLE);
                mPlayerViewFrame.setVisibility(View.VISIBLE);
            }
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDetailedDescription.setText(currentDetailedDescription);

            if (currentStepNumberInt == (maxNumberOfSteps - 1)) {
                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        getResources().getString(R.string.last_step)));

                mStepForthFrame.setVisibility(View.GONE);
            } else if (currentStepNumberInt == 0) {
                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        getResources().getString(R.string.introduction)));
                mStepBackFrame.setVisibility(View.GONE);
            } else {
                mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                        currentStepNumberInt));
            }
        }
        loadVideo(uriCurrentVideoStep);


//        if (uriCurrentVideoStep == null) {
//            mNoVideoAvailabeImage.setImageResource(R.drawable.default_thumb);
//        } else {
//            Context context = mNoVideoAvailabeImage.getContext();
////            Picasso.with(context)
////                    .load(thumb)
////                    .fit()
////                    .error(R.drawable.default_thumb)
////                    .into(viewHolder.thumbnail);
//
//            SuziLoader loader = new SuziLoader(); //Create it for once
//            loader.with(context) //Context
//                    .load(String.valueOf(uriCurrentVideoStep)) //Video path
//                    .into(mNoVideoAvailabeImage) // imageview to load the thumbnail
//                    .type("mini") // mini or micro
//                    .show(); // to show the thumbnail
//
//
//            Bitmap bmThumbnail;
//            Bitmap myPictureBitmap = BitmapFactory.decodeFile(String.valueOf(uriCurrentVideoStep));
//            myPictureBitmap = Bitmap.createScaledBitmap(myPictureBitmap, myPictureBitmap.getWidth(), myPictureBitmap.getHeight(), true);
//            // MICRO_KIND: 96 x 96 thumbnail
//            bmThumbnail = ThumbnailUtils.createVideoThumbnail(String.valueOf(uriCurrentVideoStep),
//                    MediaStore.Images.Thumbnails.MICRO_KIND);
//            mNoVideoAvailabeImage.setImageBitmap(myPictureBitmap);
//        }




        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mStepForth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int nextStepNo = getNextStepNo(currentStepNumberInt);
                    playerPosition = C.TIME_UNSET;

                    String[] nextStep = getNextPrevStep(nextStepNo);
                    int tempInt = Integer.parseInt(nextStep[1]);

                    if (tempInt == (maxNumberOfSteps - 1)) {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                "Last Step"));
                        mStepForth.setVisibility(View.GONE);
                        mStepBack.setVisibility(View.VISIBLE);
                        mStepBackFrame.setVisibility(View.VISIBLE);
                    } else {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                nextStep[1]));
                        mStepBack.setVisibility(View.VISIBLE);
                        mStepBackFrame.setVisibility(View.VISIBLE);

                    }
                    currentDetailedDescription = nextStep[3];
                    mDetailedDescription.setText(currentDetailedDescription);
                    uriCurrentVideoStep = checkUrl(nextStep[4]);
                    loadVideo(uriCurrentVideoStep);
                }
            });


            mStepBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int backStepNo = getPrevStepNo(currentStepNumberInt);
                    playerPosition = C.TIME_UNSET;

                    String[] backStep = getNextPrevStep(backStepNo);
                    int tempInt = Integer.parseInt(backStep[1]);

                    if (tempInt == 0) {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                "Introduction"));
                        mStepBack.setVisibility(View.GONE);
                        mStepForth.setVisibility(View.VISIBLE);
                        mStepForthFrame.setVisibility(View.VISIBLE);
                    } else {
                        mCurrentRecipeNo.setText(String.format(Locale.ENGLISH, "%s: %s", mCurrentRecipeNoLabel,
                                backStep[1]));
                        mStepForth.setVisibility(View.VISIBLE);
                        mStepForthFrame.setVisibility(View.VISIBLE);
                    }
                    currentDetailedDescription = backStep[3];
                    mDetailedDescription.setText(currentDetailedDescription);
                    uriCurrentVideoStep = checkUrl(backStep[4]);
                    loadVideo(uriCurrentVideoStep);
                }
            });
        }
    }

    /**
     * Release ExoPlayer.
     */
    public static void releasePlayer() {
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
    public void initializeMediaSession() {

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

    public static Uri checkUrl(String stringUrl) {

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
    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    public void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);
            if (playerPosition != C.TIME_UNSET) mExoPlayer.seekTo(playerPosition);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(isPlayWhenReady);
        }
    }

    private void loadVideo(Uri uri) {
        releasePlayer();
        initializeMediaSession();

        if (uri == null) {
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
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            playerPosition = mExoPlayer.getCurrentPosition();
            isPlayWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (uriCurrentVideoStep != null)
            initializePlayer(uriCurrentVideoStep);
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

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("video", String.valueOf(uriCurrentVideoStep));
        outState.putInt("number", currentStepNumberInt);
        outState.putString("description", String.valueOf(currentDetailedDescription));
        outState.putLong("player_position", playerPosition);
        outState.putBoolean("playstate", isPlayWhenReady);
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
