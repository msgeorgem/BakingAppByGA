package com.example.miner01.bakingappbyga;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.miner01.bakingappbyga.Utils.JsonString;
import com.example.miner01.bakingappbyga.Utils.JsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    public static Recipes recipes;
    public static String currentRecipeID;
    private int stepNumber;
    Context mContext;

    private List<String[]> recipesIngredients;
    private List<String[]> recipesSteps;

    private ArrayList<Recipes> recipesList = new ArrayList<>();


    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all recipes info ordered by creation time
        recipesList = JsonUtils.parseRecipesJson(JsonString.strJson);

    }

    @Override
    public void onDestroy() {
        recipesList.clear();
    }

    @Override
    public int getCount() {
        return recipesList.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        recipes = recipesList.get(position);
        currentRecipeID = getPositionedID(position);
        Log.i("widget", String.valueOf(currentRecipeID));

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget);

        views.setTextViewText(R.id.widget_recipe_title, recipes.getName());
        views.setTextViewText(R.id.widget_ingredients, getRecipeIngredients(currentRecipeID, recipes));

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putString(MainActivity.EXTRA_ID, recipes.getID());
        extras.putString(MainActivity.EXTRA_NAME, recipes.getName());

        int stepNumber = -9;
        extras.putInt(MainActivity.EXTRA_STEP_NUMBER, stepNumber);
        String detailedDescription = "";
        extras.putString(MainActivity.EXTRA_DESCRIPTION, detailedDescription);
        String videoUrl = "";
        extras.putString(MainActivity.EXTRA_VIDEOURL, videoUrl);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget, fillInIntent);

        return views;
    }

    private String getPositionedID(int position) {
        String currentid = "";
        String positionStr = "";
        currentid = recipes.getID();
        if (String.valueOf(position + 1).equals(currentid)) {
            positionStr = String.valueOf(position + 1);
        }
        return positionStr;
    }

    private String getRecipeIngredients(String currentRecipeID, Recipes recipes) {
        String pieceOfIngredient = "";
        String currentRecipeIDString = currentRecipeID;
        List<String[]> currentRecipeIngredients = new ArrayList<>();

        recipesIngredients = recipes.getIngredients();

        for (int i = 0; i < recipesIngredients.size(); i++) {
            String[] elements = recipesIngredients.get(i);
            String firstElementRecipe = elements[0];
            Log.i("widget get", currentRecipeIDString);
            if (firstElementRecipe.equals(currentRecipeIDString)) {
                String[] ingredientArr = new String[3];
                ingredientArr[0] = elements[1];
                ingredientArr[1] = elements[2];
                ingredientArr[2] = elements[3];
                currentRecipeIngredients.add(ingredientArr);
            }
        }
        Log.i("widget ingedients size", String.valueOf(currentRecipeIngredients.size()));
        StringBuilder builder2 = new StringBuilder();
        for (String[] details : currentRecipeIngredients) {

            String formattedString = Arrays.toString(details)
                    .replace(",", "")  //remove the commas
                    .replace("[", "")  //remove the right bracket
                    .replace("]", "")  //remove the left bracket
                    .trim();
            Log.i("widget get ingredient", formattedString);
            builder2.append("* ").append(formattedString).append("\n");
        }


        pieceOfIngredient = String.valueOf(builder2);
        return pieceOfIngredient;
    }

//    private ArrayList<String[]> getRecipeDetails(String currentRecipeID, Recipes recipes) {
//        ArrayList<String[]> currentRecipeDetails = new ArrayList<>();
//
//        recipesSteps = recipes.getSteps();
//        for (int i = 0; i < recipesSteps.size(); i++) {
//            String[] elements = recipesSteps.get(i);
//            String firstStepElement = elements[0];
//
//            if (firstStepElement.equals(currentRecipeID)) {
//                String[] elements4 = new String[4];
//
//                elements4[0] = elements[0];
//                elements4[1] = elements[1];
//                elements4[2] = elements[2];
//                elements4[3] = elements[3];
//
//                currentRecipeDetails.add(elements4);
//                }
//        }
//        return currentRecipeDetails;
//    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the ListView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

