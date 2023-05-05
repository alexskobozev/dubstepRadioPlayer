package com.wishnewjam.dubstepfm

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_STOP
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.extractor.metadata.icy.IcyInfo
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionResult
import com.wishnewjam.dubstepfm.data.DefaultStreamRepository
import com.wishnewjam.dubstepfm.data.usecase.DefaultSaveMetadataUseCase
import com.wishnewjam.dubstepfm.domain.StreamRepository
import com.wishnewjam.dubstepfm.domain.usecase.SaveMetaDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class RadioService : MediaSessionService() {

    private val streamRepository: StreamRepository = DefaultStreamRepository()
    private val saveMetadataUseCase: SaveMetaDataUseCase = DefaultSaveMetadataUseCase()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = createMediaSession()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun createMediaSession(): MediaSession {
        val player = ExoPlayer.Builder(this)
            .build()
        player.addAnalyticsListener(object : AnalyticsListener {
            override fun onMetadata(
                eventTime: AnalyticsListener.EventTime,
                metadata: Metadata
            ) {
                coroutineScope.launch {
                    Timber.d("Got metadata: $metadata")
                    updateMetadata(metadata)
                }
            }

            override fun onPlayerError(
                eventTime: AnalyticsListener.EventTime,
                error: PlaybackException
            ) {
                Timber.d(error)
                super.onPlayerError(eventTime, error)
            }

            override fun onEvents(
                player: Player,
                events: AnalyticsListener.Events
            ) = events.printDebug()
        })

        val session = MediaSession.Builder(this, player)
            .setCallback(
                object : MediaSession.Callback {
                    override fun onPlayerCommandRequest(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        playerCommand: Int
                    ): Int {
                        // controller.packageName == "com.android.bluetooth" todo   handle case
                        Timber.d("Got command to session from ${controller.packageName}: ${playerCommand.commandToString()}")
                        when (playerCommand) {
                            COMMAND_PLAY_PAUSE -> playRadio()
                            COMMAND_STOP -> stopRadio()
                        }
                        return SessionResult.RESULT_SUCCESS
                    }
                }
            )
            .build()
        return session
    }

    private fun playRadio() {
        val streamUrl = streamRepository.getStreamUrl("medium") // TODO: choose
        Timber.d("Service: play command with uri $streamUrl")
        val mediaItem = MediaItem.fromUri(streamUrl)
        val session = mediaSession!! // TODO: relax
        session.player.setMediaItem(mediaItem)
        session.player.playWhenReady = true
        session.player.prepare()
        session.player.play()
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

    private suspend fun updateMetadata(metadata: Metadata) {
        (metadata[0] as IcyInfo).title
        // streamRepository.setCurrentMetadata(metadata)
        withContext(Dispatchers.Main) {
            val mediaMetadata = MediaMetadata.Builder()
                .populateFromMetadata(metadata)
                .build()
            // mediaSession
            // val currentItem = mediaSession.currentMediaItem
            // if (currentItem != null) {
            //     val newMediaItem = currentItem.buildUpon()
            //         .setMetadata(mediaMetadata)
            //         .build()
            //     mediaSession.setMediaItem(newMediaItem)
            // }
        }
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
