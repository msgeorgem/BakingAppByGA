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
public class MainActivityToDetailTestTablet {

    public static final String RECIPE_NAME = "Brownies";
    public static final String RECIPE_DETAILED_DESC = "Recipe Introduction";

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
        assumeTrue(isScreenSw600dp());
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
        onView(withId(R.id.list_item)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Checks that the DetailActivity opens with the correct tea name displayed
        onView(withId(R.id.recipe_title)).check(matches(withText(RECIPE_NAME)));

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onView(withId(R.id.list_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Checks that the DetailStepActivity opens with the correct tea name displayed
        onView(withId(R.id.detailed_description)).check(matches(withText(RECIPE_DETAILED_DESC)));

    }
}
