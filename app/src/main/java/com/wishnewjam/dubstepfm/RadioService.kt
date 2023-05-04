package com.wishnewjam.dubstepfm

import android.app.NotificationManager
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class RadioService : MediaSessionService() {

    private val streamRepository = StreamRepository()
    private lateinit var radioNotificationManager: RadioNotificationManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        radioNotificationManager = RadioNotificationManager(
            applicationContext,
            this,
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        )
        mediaSession = createMediaSession()
    }
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun createMediaSession(): MediaSession {
        val player = ExoPlayer.Builder(this)
            .build()
        player.addAnalyticsListener(
            object : AnalyticsListener {
                override fun onPlayerError(
                    eventTime: AnalyticsListener.EventTime,
                    error: PlaybackException
                ) {
                    super.onPlayerError(eventTime, error)
                    Timber.d(error)
                }

                override fun onPlaybackStateChanged(
                    eventTime: AnalyticsListener.EventTime,
                    state: Int
                ) {
                    super.onPlaybackStateChanged(eventTime, state)
                    Timber.d("Playback state changed: $state")
                }
            }
        )
        val session = MediaSession.Builder(this, player)
            .setCallback(
                object : MediaSession.Callback {
                    override fun onPlayerCommandRequest(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        playerCommand: Int
                    ): Int {
                        // controller.packageName == "com.android.bluetooth" todo   handle case
                        Timber.d("Got command to session from ${controller.packageName}: $playerCommand")
                        when (playerCommand) {
                            COMMAND_PLAY_PAUSE -> playRadio()
                        }
                        return SessionResult.RESULT_SUCCESS
                    }
                }
            )
            .build()
        // session.setSessionCallback(object : MediaSession.SessionCallback() {
        //     override fun onPlay(session: MediaSession) {
        //         playRadio()
        //     }
        //
        //     override fun onPause(session: MediaSession) {
        //         pauseRadio()
        //     }
        //
        //     override fun onStop(session: MediaSession) {
        //         stopRadio()
        //     }
        // })
        return session
    }

    private fun playRadio() {
        val streamUrl = streamRepository.getStreamUrl("medium") // TODO: choose
        Timber.d("Service: play command with uri $streamUrl")
        val mediaItem = MediaItem.fromUri(streamUrl)
        val session = mediaSession!! // TODO: relax
        session.player.setMediaItem(mediaItem)
        session.player.play()

        coroutineScope.launch {
            val trackMetadata = streamRepository.fetchCurrentTrackMetadata()
            updateMetadata(trackMetadata)
        }

        // radioNotificationManager.showNotification(true)
    }

    private fun pauseRadio() {
        val session = mediaSession!! // TODO: relax
        session.player.pause()
        // radioNotificationManager.showNotification(false)
    }

    private fun stopRadio() {
        val session = mediaSession!! // TODO: relax
        session.player.stop()
        // radioNotificationManager.hideNotification()
    }

    private suspend fun updateMetadata(metadata: String) {
        // withContext(Dispatchers.Main) {
        //     val mediaMetadata = MediaMetadata.Builder()
        //         .setString(MediaMetadata.METADATA_KEY_TITLE, metadata)
        //         .build()
        //
        //     val currentItem = mediaSession.currentMediaItem
        //     if (currentItem != null) {
        //         val newMediaItem = currentItem.buildUpon()
        //             .setMetadata(mediaMetadata)
        //             .build()
        //         mediaSession.setMediaItem(newMediaItem)
        //     }
        // }
    }

    override fun onDestroy() {
        Timber.d("Destroy radio service")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    companion object {
        private const val ID = "RadioService"
    }
}
