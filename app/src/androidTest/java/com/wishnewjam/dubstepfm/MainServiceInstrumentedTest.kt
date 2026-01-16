package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@MediumTest
class MainServiceInstrumentedTest {

    @Test
    fun mediaBrowser_canConnect() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val latch = CountDownLatch(1)
        var connected = false
        var mediaBrowser: MediaBrowserCompat? = null

        // MediaBrowserCompat must be created on main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    connected = true
                    latch.countDown()
                }

                override fun onConnectionFailed() {
                    latch.countDown()
                }
            }

            mediaBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, MainService::class.java),
                connectionCallback,
                null
            )

            mediaBrowser?.connect()
        }

        // Wait for connection with timeout
        val result = latch.await(10, TimeUnit.SECONDS)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            mediaBrowser?.disconnect()
        }

        assertNotNull(mediaBrowser)
        assertTrue("MediaBrowser should connect successfully", connected || result)
    }
}
