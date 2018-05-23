package com.example.miner01.bakingappbyga;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

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

public class DetailedStepFragment extends Fragment {


    public static final String LOG_TAG = DetailedStepFragment.class.getSimpleName();
    private View view;


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

        VideoView videoStep = view.findViewById(R.id.video_view);

//        String currentVideoUrl = intent.getStringExtra(StepsFragment.EXTRA_VIDEOURL);

        return view;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
