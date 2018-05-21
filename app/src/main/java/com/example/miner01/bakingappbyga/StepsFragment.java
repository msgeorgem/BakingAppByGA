package com.example.miner01.bakingappbyga;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepsFragment} factory method to
 * create an instance of this fragment.
 */

public class StepsFragment extends Fragment {


    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    public static final String EXTRA_VIDEOURL = "EXTRA_VIDEOURL";

    private View view;

    private int currentRecipeIDInt;


    private List<String[]> recipesSteps;
    private ArrayList<String[]> currentRecipeDetails = new ArrayList<>();

    private static RecyclerView stepsRecyclerView;
    private RecipeDetailAdapter.OnItemClickListener mListener;
    private RecipeDetailAdapter mAdapter = new RecipeDetailAdapter(currentRecipeDetails, mListener);
    public static final String LOG_TAG = StepsFragment.class.getSimpleName();


    public StepsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_steps, container, false);

        currentRecipeIDInt = Integer.parseInt(DetailActivity.currentRecipeID);

        // Find a reference to the {@link ListView} in the layout
        stepsRecyclerView = view.findViewById(R.id.list_steps);
        currentRecipeDetails = getCurrentRecipeDetails(currentRecipeIDInt);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mListener = new RecipeDetailAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(String[] item) {

                String detailedDescription = item[1];
                String videoUrl = item[2];

                Intent intent1 = new Intent(getActivity(), DetailActivity.class);

                intent1.putExtra(EXTRA_DESCRIPTION, detailedDescription);
                intent1.putExtra(EXTRA_VIDEOURL, videoUrl);

                startActivity(intent1);
            }
        };

        mAdapter = new RecipeDetailAdapter(currentRecipeDetails, mListener);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        stepsRecyclerView.setAdapter(mAdapter);
        stepsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    private ArrayList<String[]> getCurrentRecipeDetails(int currentRecipeIDInt) {
        ArrayList<String[]> currentRecipeDetails = new ArrayList<>();

        recipesSteps = DetailActivity.recipes.getSteps();
        for (int i = 0; i < recipesSteps.size(); i++) {
            String[] elements = recipesSteps.get(i);
            int firstStepElement = Integer.parseInt(elements[0]);

            if (firstStepElement == currentRecipeIDInt) {
                String[] elements3 = new String[3];
                elements3[0] = elements[1];
                elements3[1] = elements[2];
                elements3[2] = elements[3];

                currentRecipeDetails.add(elements3);
                Log.i("current_steps9999979", elements3[0]+" "+elements3[1]+" "+ elements3[2]);
            }
        }
        return currentRecipeDetails;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
