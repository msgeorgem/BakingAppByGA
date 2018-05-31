package com.example.miner01.bakingappbyga;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miner01.bakingappbyga.databinding.FragmentStepsBinding;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.miner01.bakingappbyga.DetailActivity.mDetailBinding;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepsFragment} factory method to
 * create an instance of this fragment.
 */

public class StepsFragment extends Fragment implements ExoPlayer.EventListener {

    public static final String TAG = StepsFragment.class.getSimpleName();
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    public static final String EXTRA_VIDEOURL = "EXTRA_VIDEOURL";
    public static final String EXTRA_STEP_NUMBER = "EXTRA_STEP_NUMBER";
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private SimpleExoPlayerView mPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;

    private View view;

    private int currentRecipeIDInt;


    private List<String[]> recipesSteps;

    public static ArrayList<String[]> currentRecipeDetailsWithStepNo = new ArrayList<>();

    private static RecyclerView stepsRecyclerView;
    private RecipeDetailAdapter.OnItemClickListener mListener;
    private RecipeDetailAdapter mAdapter = new RecipeDetailAdapter(currentRecipeDetailsWithStepNo, mListener);
    public static final String LOG_TAG = StepsFragment.class.getSimpleName();

    private FragmentStepsBinding mFragmentStepsBinding;
    private Uri uriCurrentVideoStep;

    public StepsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        view = inflater.inflate(R.layout.fragment_steps, container, false);
        mFragmentStepsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_steps, container, false);
        view = mFragmentStepsBinding.getRoot();
        Context context = getActivity();

        currentRecipeIDInt = Integer.parseInt(DetailActivity.currentRecipeID);


        // Find a reference to the {@link ListView} in the layout
//        stepsRecyclerView = view.findViewById(R.id.list_steps);
        stepsRecyclerView = mFragmentStepsBinding.listSteps;
        currentRecipeDetailsWithStepNo = getCurrentRecipeDetailsWithStepNo();
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // Tablet code
        if (MainActivity.is7InchTablet) {
            mDetailBinding.part3.noVideoAvailable.setText(getResources().getString(R.string.just_click_step));
            mListener = new RecipeDetailAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(String[] item) {

                    String stepNumber = item[1];
                    String detailedDescription = item[3];
                    String videoUrl = item[4];

                    mDetailBinding.part3.detailedDescription.setText(detailedDescription);
                    uriCurrentVideoStep = DetailedStepActivity.checkUrl(videoUrl);

                    if (uriCurrentVideoStep == null) {
                        mDetailBinding.part3.playerView.setVisibility(View.GONE);
                        mDetailBinding.part3.playerViewFrame.setVisibility(View.GONE);
                        mDetailBinding.part3.noVideoAvailable.setVisibility(View.VISIBLE);
                        mDetailBinding.part3.noVideoAvailable.setText(getResources().getString(R.string.no_video_available));

                    } else {
                        mDetailBinding.part3.noVideoAvailable.setVisibility(View.GONE);
                        mDetailBinding.part3.playerView.setVisibility(View.VISIBLE);
                        mDetailBinding.part3.playerViewFrame.setVisibility(View.VISIBLE);

                        releasePlayer();
                        initializeMediaSession();
                        initializePlayer(uriCurrentVideoStep);
                    }
                }
            };


        } else {
            mListener = new RecipeDetailAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(String[] item) {

                    String stepNumber = item[1];
                    String detailedDescription = item[3];
                    String videoUrl = item[4];


                    Intent intent1 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        intent1 = new Intent(getContext(), DetailedStepActivity.class);
                    }

                    intent1.putExtra(EXTRA_DESCRIPTION, detailedDescription);
                    intent1.putExtra(EXTRA_VIDEOURL, videoUrl);
                    intent1.putExtra(EXTRA_STEP_NUMBER, stepNumber);

                    startActivity(intent1);
                }
            };
        }


        mAdapter = new RecipeDetailAdapter(currentRecipeDetailsWithStepNo, mListener);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        stepsRecyclerView.setAdapter(mAdapter);
        stepsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(mDetailBinding.part3.detailedStepContainer.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private ArrayList<String[]> getCurrentRecipeDetails(int currentRecipeIDInt) {
        ArrayList<String[]> currentRecipeDetails = new ArrayList<>();

        recipesSteps = DetailActivity.recipes.getSteps();
        for (int i = 0; i < recipesSteps.size(); i++) {
            String[] elements = recipesSteps.get(i);
            int firstStepElement = Integer.parseInt(elements[0]);

            if (firstStepElement == currentRecipeIDInt) {
                String[] elements4 = new String[4];

                elements4[0] = elements[0];
                elements4[1] = elements[1];
                elements4[2] = elements[2];
                elements4[3] = elements[3];

                currentRecipeDetails.add(elements4);
                Log.i("current_steps99", elements4[0] + " " + elements4[1] + " " + elements4[2] + " " + elements4[3]);
            }
        }
        return currentRecipeDetails;
    }

    private ArrayList<String[]> getCurrentRecipeDetailsWithStepNo() {
        ArrayList<String[]> currentRecipeDetails = new ArrayList<>();

        ArrayList<String[]> tempCurrentRecipeDetails = getCurrentRecipeDetails(currentRecipeIDInt);
        for (int i = 0; i < tempCurrentRecipeDetails.size(); i++) {
            String[] elements = tempCurrentRecipeDetails.get(i);
            String[] elements5 = new String[5];
            elements5[0] = elements[0];
            elements5[1] = String.valueOf(i);
            elements5[2] = elements[1];
            elements5[3] = elements[2];
            elements5[4] = elements[3];
            currentRecipeDetails.add(elements5);
        }
        return currentRecipeDetails;
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
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mDetailBinding.part3.playerView.setPlayer(mExoPlayer);

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
     * Release ExoPlayer.
     */
    public void releasePlayer() {
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
        mMediaSession = new MediaSessionCompat(DetailActivity.context, TAG);

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
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
//        mMediaSession.setActive(false);
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
