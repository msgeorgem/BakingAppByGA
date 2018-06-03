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
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.example.miner01.bakingappbyga.Utils.JsonString;
import com.example.miner01.bakingappbyga.Utils.JsonUtils;
import com.example.miner01.bakingappbyga.databinding.ActivityDetailBinding;

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

    public static ActivityDetailBinding mDetailBinding;
    public static Recipes recipes;
    public static String currentRecipeID;
    Context mContext;
    Cursor mCursor;
    private int mAppWidgetId;
    private List<String[]> recipesIngredients;
    private List<String[]> currentRecipeIngredients = new ArrayList<>();
    private ArrayList<Recipes> recipesList = new ArrayList<>();
    private ArrayList<String[]> ingredientsList = new ArrayList<>();
    private TextView mTitle;


    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
//        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);

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
        currentRecipeID = recipes.getID();
        Log.i("widget", currentRecipeID);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget);

        // Update the plant image
//        int imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);
//        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setTextViewText(R.id.widget_recipe_title, recipes.getName());
        views.setTextViewText(R.id.widget_ingredients, getRecipeIngredients(currentRecipeID, recipes));
//        views.setTextViewText(R.id.widget_ingredients, recipes.getID());
        // Always hide the water drop in GridView mode
//        views.setViewVisibility(R.id.widget_water_button, View.GONE);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putString(MainActivity.EXTRA_ID, recipes.getID());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_recipe_title, fillInIntent);

//        Bundle extras = new Bundle();
//        extras.putLong(MainActivity.EXTRA_ID, currentRecipeID);
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
//        views.setOnClickFillInIntent(R.id.piece_of_ingredient, fillInIntent);

        return views;

    }

    private String getRecipeIngredients(String currentRecipeID, Recipes recipes) {
        String pieceOfIngredient = "";

        recipesIngredients = recipes.getIngredients();

        for (int i = 0; i < recipesIngredients.size(); i++) {
            String[] elements = recipesIngredients.get(i);
            String firstElementRecipe = elements[0];
            Log.i("widget get", currentRecipeID);
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
            Log.i("widget get ingredient", formattedString);
            builder2.append("* ").append(formattedString).append("\n");
        }


        pieceOfIngredient = String.valueOf(builder2);
        return pieceOfIngredient;
    }

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

