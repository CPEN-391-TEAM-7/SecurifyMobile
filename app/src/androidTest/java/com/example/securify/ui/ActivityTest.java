package com.example.securify.ui;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.securify.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ActivityTest {

    private UiDevice mUiDevice;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_BACKGROUND_LOCATION");

    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }


    @Test
    public void activityTest() throws UiObjectNotFoundException {
        ViewInteraction gh = onView(
                allOf(withText("Sign In"),
                        childAtPosition(
                                allOf(withId(R.id.sign_in_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        gh.perform(click());

        UiObject mText = mUiDevice.findObject(new UiSelector().text(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getString(R.string.testing_gmail_address)));
        mText.click();

        mActivityTestRule.getActivity(). onActivityResult(1, 1, new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        pressBack();

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.load_more_button), withText("Load More"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                9),
                        isDisplayed()));
        materialButton.perform(click());
        waitAsync(3000);

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.filter_button), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction switchCompat = onView(
                allOf(withId(R.id.switch_compat),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        switchCompat.perform(click());

        ViewInteraction switchCompat2 = onView(
                allOf(withId(R.id.switch_compat),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        switchCompat2.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.filter_list_selector),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                11),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction materialTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        materialTextView.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.filter_apply_button), withText("Apply"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                13),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.filter_list_selector),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                11),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction materialTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        materialTextView2.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.filter_apply_button), withText("Apply"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                13),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.filter_start_date_text),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(click());
        appCompatEditText.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.filter_apply_button), withText("Apply"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                13),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.filter_end_date_text),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                7),
                        isDisplayed()));
        appCompatEditText2.perform(click());
        appCompatEditText2.perform(click());

        ViewInteraction materialButton7 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.filter_start_time_text),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                5),
                        isDisplayed()));
        appCompatEditText3.perform(click());
        appCompatEditText3.perform(click());

        ViewInteraction materialButton8 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton8.perform(scrollTo(), click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.filter_end_time_text),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                9),
                        isDisplayed()));
        appCompatEditText4.perform(click());
        appCompatEditText4.perform(click());

        ViewInteraction materialButton9 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton9.perform(scrollTo(), click());

        ViewInteraction materialButton10 = onView(
                allOf(withId(R.id.filter_apply_button), withText("Apply"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                13),
                        isDisplayed()));
        materialButton10.perform(click());

        ViewInteraction materialAutoCompleteTextView = onView(
                allOf(withId(R.id.filter_domain_name_autocomplete),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                1),
                        isDisplayed()));
        materialAutoCompleteTextView.perform(click());

        ViewInteraction materialAutoCompleteTextView2 = onView(
                allOf(withId(R.id.filter_domain_name_autocomplete),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                1),
                        isDisplayed()));
        materialAutoCompleteTextView2.perform(replaceText("google.co.id"), closeSoftKeyboard());
        
        ViewInteraction materialButton11 = onView(
                allOf(withId(R.id.filter_apply_button), withText("Apply"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                13),
                        isDisplayed()));
        materialButton11.perform(click());

        ViewInteraction materialButton12 = onView(
                allOf(withId(R.id.filter_reset_button), withText("Reset"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                12),
                        isDisplayed()));
        materialButton12.perform(click());

        ViewInteraction materialButton13 = onView(
                allOf(withId(R.id.filter_apply_button), withText("Apply"),
                        childAtPosition(
                                allOf(withId(R.id.filter_menu),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                5)),
                                13),
                        isDisplayed()));
        materialButton13.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_activity), withContentDescription("Activity"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction materialTextView4 = onView(
                allOf(withId(R.id.domain_title), withText("Domain"),
                        childAtPosition(
                                allOf(withId(R.id.activity_list_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                6)),
                                0),
                        isDisplayed()));
        materialTextView4.perform(click());

        ViewInteraction materialTextView5 = onView(
                allOf(withId(R.id.domain_title), withText("Domain"),
                        childAtPosition(
                                allOf(withId(R.id.activity_list_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                6)),
                                0),
                        isDisplayed()));
        materialTextView5.perform(click());

        ViewInteraction materialTextView6 = onView(
                allOf(withId(R.id.timestamp_title), withText("Timestamp"),
                        childAtPosition(
                                allOf(withId(R.id.activity_list_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                6)),
                                1),
                        isDisplayed()));
        materialTextView6.perform(click());

        ViewInteraction materialTextView7 = onView(
                allOf(withId(R.id.timestamp_title), withText("Timestamp"),
                        childAtPosition(
                                allOf(withId(R.id.activity_list_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                6)),
                                1),
                        isDisplayed()));
        materialTextView7.perform(click());

        ViewInteraction materialTextView8 = onView(
                allOf(withId(R.id.list_title), withText("List"),
                        childAtPosition(
                                allOf(withId(R.id.activity_list_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                6)),
                                2),
                        isDisplayed()));
        materialTextView8.perform(click());

        ViewInteraction materialTextView9 = onView(
                allOf(withId(R.id.list_title), withText("List"),
                        childAtPosition(
                                allOf(withId(R.id.activity_list_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                6)),
                                2),
                        isDisplayed()));
        materialTextView9.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction materialButton14 = onView(
                allOf(withId(R.id.sign_out_button), withText("Sign Out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                1),
                        isDisplayed()));
        materialButton14.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static void waitAsync(long milliseconds) {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    return null;
                }
            }.get(milliseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
