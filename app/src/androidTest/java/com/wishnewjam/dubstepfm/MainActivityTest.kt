package com.wishnewjam.dubstepfm

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun playButton_isDisplayed() {
        onView(withId(R.id.tv_play))
            .check(matches(isDisplayed()))
    }

    @Test
    fun stopButton_isDisplayed() {
        onView(withId(R.id.tv_stop))
            .check(matches(isDisplayed()))
    }

    @Test
    fun toolbar_isDisplayed() {
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun playButton_hasCorrectText() {
        onView(withId(R.id.tv_play))
            .check(matches(withText(R.string.play)))
    }

    @Test
    fun stopButton_hasCorrectText() {
        onView(withId(R.id.tv_stop))
            .check(matches(withText(R.string.stop)))
    }

    @Test
    fun statusImageView_isDisplayed() {
        onView(withId(R.id.iv_status))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickPlayButton_doesNotCrash() {
        onView(withId(R.id.tv_play))
            .perform(click())
        // If we get here without exception, the test passes
    }

    @Test
    fun clickStopButton_doesNotCrash() {
        onView(withId(R.id.tv_stop))
            .perform(click())
        // If we get here without exception, the test passes
    }

    @Test
    fun nowPlayingTextView_isDisplayed() {
        onView(withId(R.id.tv_nowplaying))
            .check(matches(isDisplayed()))
    }
}
