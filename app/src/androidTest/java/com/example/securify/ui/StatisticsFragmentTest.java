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
public class StatisticsFragmentTest {

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
    public void statisticsFragmentTest() throws UiObjectNotFoundException {
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

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_statistics), withContentDescription("Statistics"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        waitAsync(3000);
        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.count_text), withText("Count"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                1)));
        materialTextView.perform(scrollTo(), click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.count_text), withText("Count"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                1)));
        materialTextView2.perform(scrollTo(), click());

        ViewInteraction materialTextView3 = onView(
                allOf(withId(R.id.list_text), withText("List"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                2)));
        materialTextView3.perform(scrollTo(), click());

        ViewInteraction materialTextView4 = onView(
                allOf(withId(R.id.list_text), withText("List"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                2)));
        materialTextView4.perform(scrollTo(), click());

        ViewInteraction materialTextView5 = onView(
                allOf(withId(R.id.list_text), withText("List"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                2)));
        materialTextView5.perform(scrollTo(), click());

        ViewInteraction materialTextView6 = onView(
                allOf(withId(R.id.domain_text), withText("Domain"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                0)));
        materialTextView6.perform(scrollTo(), click());

        ViewInteraction materialTextView7 = onView(
                allOf(withId(R.id.domain_text), withText("Domain"),
                        childAtPosition(
                                allOf(withId(R.id.top_domains_titles),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                8)),
                                0)));
        materialTextView7.perform(scrollTo(), click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.top_domains_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatSpinner.perform(scrollTo(), click());

        DataInteraction materialTextView8 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        materialTextView8.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.top_domains_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatSpinner2.perform(scrollTo(), click());

        DataInteraction materialTextView9 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        materialTextView9.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.top_domains_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatSpinner3.perform(scrollTo(), click());

        DataInteraction materialTextView10 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(3);
        materialTextView10.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.top_domains_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatSpinner4.perform(scrollTo(), click());

        DataInteraction materialTextView11 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(4);
        materialTextView11.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.sign_out_button), withText("Sign Out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());
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
