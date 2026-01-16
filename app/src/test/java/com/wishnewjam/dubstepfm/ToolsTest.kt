package com.wishnewjam.dubstepfm

import android.content.Context
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class ToolsTest {

    @Before
    fun setUp() {
        // Plant a debug tree for testing
        if (Timber.forest().isEmpty()) {
            Timber.plant(Timber.DebugTree())
        }
    }

    @Test
    fun `logDebug should not crash when called`() {
        // This test verifies that logDebug doesn't throw an exception
        Tools.logDebug { "Test log message" }
        // If we get here, the test passes
    }

    @Test
    fun `logDebug should evaluate lambda`() {
        var lambdaCalled = false
        val testMessage = "Test message"

        Tools.logDebug {
            lambdaCalled = true
            testMessage
        }

        assert(lambdaCalled) { "Lambda should be called" }
    }

    @Test
    fun `toastDebug should not crash`() {
        val mockContext = mockk<Context>(relaxed = true)

        // toastDebug is currently a no-op, but should not throw
        Tools.toastDebug({ "Test toast" }, mockContext)
    }

    @Test
    fun `logDebug should handle empty string`() {
        Tools.logDebug { "" }
        // If we get here, the test passes
    }

    @Test
    fun `logDebug should handle long messages`() {
        val longMessage = "a".repeat(10000)
        Tools.logDebug { longMessage }
        // If we get here, the test passes
    }
}
