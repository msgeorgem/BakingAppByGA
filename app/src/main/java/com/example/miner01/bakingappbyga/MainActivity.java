package com.example.miner01.bakingappbyga;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.miner01.bakingappbyga.Utils.JsonString;
import com.example.miner01.bakingappbyga.Utils.JsonUtils;

import java.util.ArrayList;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_NAME = "EXTRA_NAME";


    public static final String LOG_TAG = MainActivity.class.getName();
    public static Context context;
    private ArrayList<Recipes> recipesList = new ArrayList<>();
    private RecyclerView recipesRecyclerView;
    public static int sizeXLarge;
    public static boolean isSizeXLarge;

    /**
     * Adapter for the list of recipes
     */
    private RecipeMainAdapter.OnItemClickListener mListener;
    private RecipeMainAdapter mAdapter = new RecipeMainAdapter(recipesList, mListener);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        RecipeDetailAdapter.selectedIndex = -9;

        // Find a reference to the {@link ListView} in the layout
        recipesRecyclerView = findViewById(R.id.list_item);
        recipesList = JsonUtils.parseRecipesJson(JsonString.strJson);

        sizeXLarge = SCREENLAYOUT_SIZE_XLARGE; // For sizeXLarge Tablets
        isSizeXLarge = context.getResources().getConfiguration()
                .isLayoutSizeAtLeast(sizeXLarge);


        if (isSizeXLarge) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recipesRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            } else {
                recipesRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            }
        } else {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recipesRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            } else {
                recipesRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            }

        }
        mListener = new RecipeMainAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Recipes item) {

                String currentRecipeID = item.getID();
                String currentRecipeName = item.getName();

                Intent intent1 = new Intent(getApplicationContext(), DetailActivity.class);

                intent1.putExtra(EXTRA_ID, currentRecipeID);
                intent1.putExtra(EXTRA_NAME, currentRecipeName);

                startActivity(intent1);
            }
        };
        mAdapter = new RecipeMainAdapter(recipesList, mListener);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        recipesRecyclerView.setAdapter(mAdapter);
        recipesRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
