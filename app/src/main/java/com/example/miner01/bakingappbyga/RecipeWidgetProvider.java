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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import static com.example.miner01.bakingappbyga.MainActivity.isSizeXLarge;


public class RecipeWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_NAME = "Recipe_name";
    public static final String EXTRA_ID = "Recipe_Id";
    static Intent appIntent;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get current width to decide on single plant vs garden grid view
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;
//        if (width < 300) {
//            rv = getSinglePlantRemoteView(context, imgRes, plantId, showWater);
//        } else {
        rv = getRecipeListRemoteView(context);
//        }
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    /**
     * Updates all widget instances given the widget Ids and display information
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param appWidgetIds     Array of widget Ids to be updated
     */
    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the single plant mode widget
     *
     * @param context   The context
     * @param imgRes    The image resource of the plant image to be displayed
     * @param plantId   The database plant Id for watering button functionality
     * @param showWater Boolean to either show/hide the water drop
     * @return The RemoteViews for the single plant mode widget
     */
    private static RemoteViews getSinglePlantRemoteView(Context context, int imgRes, long plantId, boolean showWater) {
        // Set the click handler to open the DetailActivity for plant ID,
        // or the MainActivity if plant ID is invalid
        Intent intent;
        if (plantId == 0) {
            intent = new Intent(context, MainActivity.class);
        } else { // Set on click to open the corresponding detail activity
            Log.d(RecipeWidgetProvider.class.getSimpleName(), "plantId=" + plantId);
            intent = new Intent(context, DetailActivity.class);
            intent.putExtra(MainActivity.EXTRA_ID, plantId);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        // Update image and text
//        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setTextViewText(R.id.widget_ingredients, String.valueOf(plantId));
        // Show/Hide the water drop button
//        if (showWater) views.setViewVisibility(R.id.widget_water_button, View.VISIBLE);
//        else views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
//        // Widgets allow click handlers to only launch pending intents
//        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);
//        // Add the wateringservice click handler
//        Intent wateringIntent = new Intent(context, PlantWateringService.class);
//        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
//        // Add the plant ID as extra to water only that plant when clicked
//        wateringIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
//        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);
        return views;
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the GridView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the GridView mode widget
     */
    private static RemoteViews getRecipeListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_stack_view, intent);
        // Set the DetailActivity intent to launch when clicked
        if (isSizeXLarge) {
            appIntent = new Intent(context, DetailedStepActivity.class);
        } else {
            appIntent = new Intent(context, DetailActivity.class);
        }
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_stack_view, appPendingIntent);
        // Handle empty gardens
        views.setEmptyView(R.id.widget_stack_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
//        PlantWateringService.startActionUpdatePlantWidgets(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
