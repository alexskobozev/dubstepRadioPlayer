package com.wishnewjam.playback.data

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_STOP
import androidx.media3.common.Rating
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import com.wishnewjam.commons.android.apiContainer
import com.wishnewjam.di.getFeature
import com.wishnewjam.playback.data.di.DaggerRadioServiceControllerComponent
import com.wishnewjam.playback.data.usecase.SaveMetaDataUseCase
import com.wishnewjam.playback.domain.PlayerState
import com.wishnewjam.playback.domain.PlayerStateRepository
import com.wishnewjam.playback.domain.RadioServiceController
import com.wishnewjam.stream.domain.StreamRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class DefaultRadioServiceController @Inject constructor(
    private val playerStateRepository: PlayerStateRepository,
) : RadioServiceController {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var mediaSession: MediaSession? = null

    lateinit var mediaSessionService: MediaSessionService

    @Inject
    lateinit var saveMetaDataUseCase: SaveMetaDataUseCase

    @Inject
    lateinit var streamRepository: StreamRepository

    override fun create(mediaSessionService: MediaSessionService) {
        DaggerRadioServiceControllerComponent.factory().create(
            metadataApi = mediaSessionService.apiContainer().getFeature(),
            streamApi = mediaSessionService.apiContainer().getFeature(),
        ).inject(this)
        this.mediaSessionService = mediaSessionService
        mediaSession = createMediaSession()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun createMediaSession(): MediaSession {
        val player = ExoPlayer.Builder(mediaSessionService)
            .build()
        player.addAnalyticsListener(object : AnalyticsListener {
            override fun onMetadata(
                eventTime: AnalyticsListener.EventTime,
                metadata: Metadata
            ) {
                Timber.d("onMetadata() called with: eventTime = $eventTime, metadata = $metadata")
                updateMetadata(metadata)
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
            ) {
                events.printDebug()
                for (i in 0 until events.size()) {
                    when (events[i]) {
                        Player.EVENT_IS_PLAYING_CHANGED -> if (player.isPlaying) {
                            playerStateRepository.setCurrentState(PlayerState.PLAYING)
                        } else {
                            playerStateRepository.setCurrentState(PlayerState.STOPPED)
                        }

//                        Player.EVENT_MEDIA_METADATA_CHANGED -> onMetadataChanged(player.mediaMetadata)
                    }
                }
            }
        })

        val mediaSession = MediaSession.Builder(mediaSessionService, player)
            .setCallback(
                object : MediaSession.Callback {
                    @Deprecated("Deprecated in Java")
                    override fun onPlayerCommandRequest(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        playerCommand: Int
                    ): Int {
                        Timber.d(
                            "onPlayerCommandRequest() called with: session = $session, controller = $controller, playerCommand = $playerCommand"
                        )
                        return requestPlayerCommand(controller, playerCommand)
                    }

                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ): MediaSession.ConnectionResult {
                        Timber.d(
                            "onConnect() called with: session = $session, controller = $controller"
                        )
                        return super.onConnect(session, controller)
                    }

                    override fun onPostConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ) {
                        Timber.d(
                            "onPostConnect() called with: session = $session, controller = $controller"
                        )
                        super.onPostConnect(session, controller)
                    }

                    override fun onDisconnected(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ) {
                        Timber.d(
                            "onDisconnected() called with: session = $session, controller = $controller"
                        )
                        super.onDisconnected(session, controller)
                    }

                    override fun onSetRating(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        mediaId: String,
                        rating: Rating
                    ): ListenableFuture<SessionResult> {
                        Timber.d(
                            "onSetRating() called with: session = $session, controller = $controller, mediaId = $mediaId, rating = $rating"
                        )
                        return super.onSetRating(session, controller, mediaId, rating)
                    }

                    override fun onSetRating(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        rating: Rating
                    ): ListenableFuture<SessionResult> {
                        Timber.d(
                            "onSetRating() called with: session = $session, controller = $controller, rating = $rating"
                        )
                        return super.onSetRating(session, controller, rating)
                    }

                    override fun onCustomCommand(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        customCommand: SessionCommand,
                        args: Bundle
                    ): ListenableFuture<SessionResult> {
                        Timber.d(
                            "onCustomCommand() called with: session = $session, controller = $controller, customCommand = $customCommand, args = $args"
                        )
                        return super.onCustomCommand(session, controller, customCommand, args)
                    }

                    override fun onAddMediaItems(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        mediaItems: MutableList<MediaItem>
                    ): ListenableFuture<MutableList<MediaItem>> {
                        Timber.d(
                            "onAddMediaItems() called with: mediaSession = $mediaSession, controller = $controller, mediaItems = $mediaItems"
                        )
                        return super.onAddMediaItems(mediaSession, controller, mediaItems)
                    }

                    override fun onSetMediaItems(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        mediaItems: MutableList<MediaItem>,
                        startIndex: Int,
                        startPositionMs: Long
                    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                        Timber.d(
                            "onSetMediaItems() called with: mediaSession = $mediaSession, controller = $controller, mediaItems = $mediaItems, startIndex = $startIndex, startPositionMs = $startPositionMs"
                        )
                        return super.onSetMediaItems(
                            mediaSession,
                            controller,
                            mediaItems,
                            startIndex,
                            startPositionMs
                        )
                    }

                    override fun onPlaybackResumption(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                        Timber.d(
                            "onPlaybackResumption() called with: mediaSession = $mediaSession, controller = $controller"
                        )
                        return super.onPlaybackResumption(mediaSession, controller)
                    }

                    override fun onMediaButtonEvent(
                        session: MediaSession,
                        controllerInfo: MediaSession.ControllerInfo,
                        intent: Intent
                    ): Boolean {
                        Timber.d(
                            "onMediaButtonEvent() called with: session = $session, controllerInfo = $controllerInfo, intent = $intent"
                        )
                        return super.onMediaButtonEvent(session, controllerInfo, intent)
                    }
                }
            )
            .build()
        return mediaSession
    }

//    private fun onMetadataChanged(mediaMetadata: MediaMetadata) {
//        Timber.d("onMetadataChanged() called with: mediaMetadata = $mediaMetadata")
//    }

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
        val mediaItem = MediaItem.Builder()
            .setMediaId("dubstep")
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtworkUri(Uri.parse("android.resource://com.wishnewjam.dubstepfm/" + R.drawable.dsfm_cover))
                    .build()
            )
            .build()
        mediaSession?.let { session ->
            session.player.setMediaItem(mediaItem)
            session.player.playWhenReady = true
            session.player.prepare()
            session.player.play()
        }
    }

    private fun stopRadio() {
        val session = mediaSession!! // TODO: relax
        session.player.stop()
        // radioNotificationManager.hideNotification()
    }

    private fun updateMetadata(metadata: Metadata) =
        saveMetaDataUseCase.saveMetaData(metadata)

    override fun destroy() {
        Timber.d("Destroy radio service")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
    }

    override fun getSession(): MediaSession? {
        return mediaSession
    }

    companion object {
        private const val ID = "RadioService"
    }
}
