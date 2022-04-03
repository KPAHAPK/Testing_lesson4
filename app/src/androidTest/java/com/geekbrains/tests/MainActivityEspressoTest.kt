package com.geekbrains.tests

import android.app.Activity
import android.app.Instrumentation
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.geekbrains.tests.view.details.DetailsActivity
import com.geekbrains.tests.view.search.MainActivity
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var result: Instrumentation.ActivityResult

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
    }

    @Test
    fun activitySearch_IsWorking() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())

        if (BuildConfig.FLAVOR == "fake") {
            onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 42")))
        } else {
            onView(isRoot()).perform(delay())
            onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 2283")))
        }
    }

    @Test
    fun activitySearch_start() {
        with(onView(withId(R.id.toDetailsActivityButton))) {
            check(matches(withText("to details")))
            check(matches(isEnabled()))
            check(matches(isDisplayed()))
        }

        with(onView(withId(R.id.searchEditText))) {
            check(matches(withHint("Enter keyword e.g. android")))
            check(matches(isDisplayed()))
        }
    }

    @Test
    fun activitySearch_toDetailsActivityButton_isWorking() {
        onView(withId(R.id.toDetailsActivityButton)).perform(click())

        Intents.init()
        intending(IntentMatchers.toPackage(DetailsActivity::class.java.name)).respondWith(result)
    }

    private fun delay(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $2 seconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(2000)
            }
        }
    }

    @After
    fun close() {
        Intents.release()
        scenario.close()
    }
}
