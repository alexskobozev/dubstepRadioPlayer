package com.wishnewjam.dubstepfm.playback

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.MediaBrowserServiceCompat
import com.wishnewjam.dubstepfm.notification.LogoProvider
import com.wishnewjam.dubstepfm.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainService : MediaBrowserServiceCompat() {

    companion object {
        const val NOTIFICATION_ID = 43432
    }

    private var audioFocusRequest: AudioFocusRequestCompat? = null

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    @Inject
    lateinit var logoProvider: LogoProvider

    private lateinit var mediaCore: MediaCore

    override fun onLoadChildren(parentId: String,
                                result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): BrowserRoot {
        return BrowserRoot("", null)
    }

    override fun onCreate() {
        super.onCreate()
        val stopForeground = { stopForeground(true) }
        val startForeground = { n: Notification -> startForeground(NOTIFICATION_ID, n) }
        mediaCore = MediaCore(notificationBuilder, logoProvider, startForeground, stopForeground)
        mediaCore.init(this)
        sessionToken = mediaCore.token
    }

    override fun onStartCommand(intent: Intent?,
                                flags: Int,
                                startId: Int): Int {
        if (intent != null) {
            mediaCore.handleIntent(intent)
        }

        // TODO: 15/06/2021 что если интент null
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaCore.destroy()
    }
}
