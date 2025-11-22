// Test 2: see if the system can appropriately handle someone without registration attempting to login

package com.example.logintest.domain;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest2 {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkUserName() {
        onView(withId(R.id.user_name)).perform(typeText("mingyu.kim@svt.edu"), closeSoftKeyboard());
        onView(withId(R.id.user_name)).check(matches(withText("mingyu.kim@svt.edu")));
        onView(withId(R.id.user_name)).check(matches(not(withText("mingyu.kim@svt.edu"))));

        onView(withId(R.id.user_password)).perform(typeText("hellolittlefellow123"), closeSoftKeyboard());
        onView(withId(R.id.user_password)).check(matches(withText("hellolittlefellow12")));
        onView(withId(R.id.user_password)).check(matches(not(withText("hellolittlefellow12"))));
    }
}
