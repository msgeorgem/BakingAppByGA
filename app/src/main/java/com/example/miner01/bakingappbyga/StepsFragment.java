package com.example.miner01.bakingappbyga;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miner01.bakingappbyga.databinding.FragmentDetailedStepBinding;
import com.example.miner01.bakingappbyga.databinding.FragmentStepsBinding;

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

public class StepsFragment extends Fragment {


    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    public static final String EXTRA_VIDEOURL = "EXTRA_VIDEOURL";
    public static final String EXTRA_STEP_NUMBER = "EXTRA_STEP_NUMBER";

    private View view;

    private int currentRecipeIDInt;


    private List<String[]> recipesSteps;

    public static ArrayList<String[]> currentRecipeDetailsWithStepNo = new ArrayList<>();

    private static RecyclerView stepsRecyclerView;
    private RecipeDetailAdapter.OnItemClickListener mListener;
    private RecipeDetailAdapter mAdapter = new RecipeDetailAdapter(currentRecipeDetailsWithStepNo, mListener);
    public static final String LOG_TAG = StepsFragment.class.getSimpleName();
    private DetailedStepFragment mDetailedStepFragment;
    private FragmentDetailedStepBinding mFragmentDetailedStepsBinding;
    private FragmentStepsBinding mFragmentStepsBinding;

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

        currentRecipeIDInt = Integer.parseInt(DetailActivity.currentRecipeID);


        // Find a reference to the {@link ListView} in the layout
//        stepsRecyclerView = view.findViewById(R.id.list_steps);
        stepsRecyclerView = mFragmentStepsBinding.listSteps;
        currentRecipeDetailsWithStepNo = getCurrentRecipeDetailsWithStepNo();
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mListener = new RecipeDetailAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(String[] item) {

                String stepNumber = item[1];
                String detailedDescription = item[3];
                String videoUrl = item[4];

                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_DESCRIPTION, detailedDescription);
                bundle.putString(EXTRA_VIDEOURL, videoUrl);
                bundle.putString(EXTRA_STEP_NUMBER, stepNumber);


                mDetailedStepFragment = new DetailedStepFragment();
                mDetailedStepFragment.setArguments(bundle);

                replaceFragment(mDetailedStepFragment);
                mDetailBinding.part1.ingredients.setVisibility(View.GONE);
                mDetailBinding.part2.stepsContainer.setVisibility(View.GONE);
                mDetailBinding.part3.detailedStepContainer.setVisibility(View.VISIBLE);
            }
        };

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




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
