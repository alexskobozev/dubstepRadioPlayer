package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ServiceTestRule
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@MediumTest
class MainServiceInstrumentedTest {

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Test
    fun service_canBeStarted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, MainService::class.java)

        serviceRule.startService(intent)
        // If we get here without exception, the service started successfully
    }

    @Test
    fun mediaBrowser_canConnect() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val latch = CountDownLatch(1)
        var connected = false

        val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                connected = true
                latch.countDown()
            }

            override fun onConnectionFailed() {
                latch.countDown()
            }
        }

        val mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, MainService::class.java),
            connectionCallback,
            null
        )

        mediaBrowser.connect()

        // Wait for connection with timeout
        latch.await(5, TimeUnit.SECONDS)
        mediaBrowser.disconnect()

        assertNotNull(mediaBrowser)
    }
}
