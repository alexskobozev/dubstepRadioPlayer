package com.wishnewjam.playback.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_STOP
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionResult
import com.wishnewjam.commons.android.apiContainer
import com.wishnewjam.di.getFeature
import com.wishnewjam.playback.data.di.DaggerRadioServiceComponent
import com.wishnewjam.playback.data.usecase.SaveMetaDataUseCase
import com.wishnewjam.stream.domain.StreamRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class RadioService : MediaSessionService() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var saveMetaDataUseCase: SaveMetaDataUseCase

    @Inject
    lateinit var streamRepository: StreamRepository

    override fun onCreate() {
        super.onCreate()
        DaggerRadioServiceComponent.factory().create(
            metadataApi = apiContainer().getFeature(),
            streamApi = apiContainer().getFeature(),
        ).inject(this)
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

        return MediaSession.Builder(this, player)
            .setCallback(
                object : MediaSession.Callback {
                    override fun onPlayerCommandRequest(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        playerCommand: Int
                    ): Int = requestPlayerCommand(controller, playerCommand)
                }
            )
            .build()
    }

    private fun requestPlayerCommand(
        controller: MediaSession.ControllerInfo,
        playerCommand: Int
    ): Int {
        // controller.packageName == "com.android.bluetooth" todo   handle case
        Timber.d("Got command to session from ${controller.packageName}: ${playerCommand.commandToString()}")
        when (playerCommand) {
            COMMAND_PLAY_PAUSE -> uiScope.launch { playRadio() }
            COMMAND_STOP -> stopRadio()
        }
        return SessionResult.RESULT_SUCCESS
    }

    private suspend fun playRadio() {
        val streamUrl = streamRepository.currentStreamUrl.last() // TODO: choose
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

    private suspend fun updateMetadata(metadata: androidx.media3.common.Metadata) {
        saveMetaDataUseCase.saveMetaData(metadata)

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
