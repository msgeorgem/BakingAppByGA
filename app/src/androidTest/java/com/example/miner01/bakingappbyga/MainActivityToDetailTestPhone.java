package com.example.miner01.bakingappbyga;


import android.content.res.Resources;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assume.assumeTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityToDetailTestPhone {

    public static final String RECIPE_NAME = "Nutella Pie";
    public static final String RECIPE_DETAILED_DESC = "2. Whisk the graham cracker crumbs, 50 grams" +
            " (1/4 cup) of sugar, and 1/2 teaspoon of salt together in a medium bowl. Pour the melted" +
            " butter and 1 teaspoon of vanilla into the dry ingredients and stir together until evenly mixed.";
    public static final String STEP_NAME = "Last Step";
    public static final String STEP_NO = "Current Step: 2";
    public static final String STEP_NO1 = "Current Step: 1";
    public static final String STEP_NO3 = "Current Step: 3";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated after
     * the test and methods annotated with @After are complete. This rule allows you to directly
     * access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        assumeTrue(!isScreenSw600dp());
        // other setup
    }

    private boolean isScreenSw600dp() {

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        float heightDp = displayMetrics.heightPixels / displayMetrics.density;
        float screenSw = Math.min(widthDp, heightDp);
        return screenSw >= 600;
    }

    /**
     * Clicks on a RecyclerView item and checks it opens up the DetailActivity with the correct details.
     */
    @Test
    public void clickRecyclerViewItem_OpensDetailActivity() {

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onView(withId(R.id.list_item)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Checks that the DetailActivity opens with the correct tea name displayed
        onView(withId(R.id.recipe_title)).check(matches(withText(RECIPE_NAME)));

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onView(withId(R.id.list_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        // Checks that the DetailStepActivity opens with the correct tea name displayed
        onView(withId(R.id.detailed_description)).check(matches(withText(RECIPE_DETAILED_DESC)));

        // Check the initial quantity variable is 2
        onView((withId(R.id.step_number))).check(matches(withText(STEP_NO)));

        // Click on step back
        onView((withId(R.id.step_back))).perform(click());

        // Verify that the step back button decreases the  by 1
        onView(withId(R.id.step_number)).check(matches(withText(STEP_NO1)));

        // Click on step forth
        onView((withId(R.id.step_forth))).perform(click());
        // Click on step forth
        onView((withId(R.id.step_forth))).perform(click());
        // Verify that the step back button decreases the  by 1
        onView(withId(R.id.step_number)).check(matches(withText(STEP_NO3)));
    }
}
