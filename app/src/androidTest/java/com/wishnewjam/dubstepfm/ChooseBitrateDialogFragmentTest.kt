package com.wishnewjam.dubstepfm

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ChooseBitrateDialogFragmentTest {

    @Test
    fun fragment_displaysAllBitrateOptions() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_24))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_bitrate_64))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_bitrate_128))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_bitrate_256))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displaysPrivacyPolicyLink() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_privacy_policy))
            .check(matches(isDisplayed()))
    }

    @Test
    fun bitrate24_hasCorrectLabel() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_24))
            .check(matches(withText("24 kbps")))
    }

    @Test
    fun bitrate64_hasCorrectLabel() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_64))
            .check(matches(withText("64 kbps")))
    }

    @Test
    fun bitrate128_hasCorrectLabel() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_128))
            .check(matches(withText("128 kbps")))
    }

    @Test
    fun bitrate256_hasCorrectLabel() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_256))
            .check(matches(withText("256 kbps")))
    }

    @Test
    fun clickBitrate24_doesNotCrash() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_24))
            .perform(click())
        // Fragment should dismiss, test passes if no crash
    }

    @Test
    fun clickBitrate64_doesNotCrash() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_64))
            .perform(click())
        // Fragment should dismiss, test passes if no crash
    }

    @Test
    fun clickBitrate128_doesNotCrash() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_128))
            .perform(click())
        // Fragment should dismiss, test passes if no crash
    }

    @Test
    fun clickBitrate256_doesNotCrash() {
        launchFragmentInContainer<ChooseBitrateDialogFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.tv_bitrate_256))
            .perform(click())
        // Fragment should dismiss, test passes if no crash
    }
}
