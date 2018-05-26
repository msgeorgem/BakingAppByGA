package com.example.miner01.bakingappbyga;


import android.app.FragmentManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.miner01.bakingappbyga.Utils.JsonString;
import com.example.miner01.bakingappbyga.Utils.JsonUtils1;
import com.example.miner01.bakingappbyga.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DetailActivity extends AppCompatActivity implements StepsFragment.OnFragmentInteractionListener {


    private StepsFragment mStepsFragment;

    public static ActivityDetailBinding mDetailBinding;
    public static Recipes recipes;
    public static String currentRecipeID;
    private int currentRecipeIDInt;
    private List<String[]> recipesIngredients;
    private List<String[]> currentRecipeIngredients = new ArrayList<>();
    private Uri mCurrentItemUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        mCurrentItemUri = intent.getData();


        recipes = JsonUtils1.parseRecipesJson(JsonString.strJson);

        currentRecipeID = intent.getStringExtra(MainActivity.EXTRA_ID);
        currentRecipeIDInt = Integer.parseInt(currentRecipeID);

        Log.i("detail activity", currentRecipeID);

        recipesIngredients = recipes.getIngredients();

        for (int i = 0; i < recipesIngredients.size(); i++) {
            String[] elements = recipesIngredients.get(i);
            String firstElementRecipe = elements[0];

            if (firstElementRecipe.equals(currentRecipeID)) {
                String[] ingredientArr = new String[3];
                ingredientArr[0] = elements[1];
                ingredientArr[1] = elements[2];
                ingredientArr[2] = elements[3];
                currentRecipeIngredients.add(ingredientArr);
            }
        }

        StringBuilder builder2 = new StringBuilder();
        for (String[] details : currentRecipeIngredients) {

            String formattedString = Arrays.toString(details)
                    .replace(",", "")  //remove the commas
                    .replace("[", "")  //remove the right bracket
                    .replace("]", "")  //remove the left bracket
                    .trim();
            Log.i("current_detail", formattedString);
            builder2.append("* ").append(formattedString).append("\n");
        }

        mDetailBinding.part1.ingredients.setText(builder2.toString());
        mDetailBinding.part1.ingredients.setVisibility(View.VISIBLE);
        mDetailBinding.part2.stepsContainer.setVisibility(View.VISIBLE);
        mDetailBinding.part5.detailNavigationTemp.setVisibility(View.GONE);
//        mDetailBinding.part3.detailedStepContainer.setVisibility(View.VISIBLE);

        mStepsFragment = new StepsFragment();
        FragmentManager managerA = getFragmentManager();
        managerA.beginTransaction()
                .replace(mDetailBinding.part2.stepsContainer.getId(), mStepsFragment, mStepsFragment.getTag())
                .commit();


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}
