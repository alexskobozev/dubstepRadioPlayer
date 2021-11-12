package com.wishnewjam.dubstepfm

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.wishnewjam.dubstepfm.data.repository.RadioStreamRepositoryImpl
import com.wishnewjam.dubstepfm.playback.MainService
import com.wishnewjam.dubstepfm.ui.DubstepApp
import com.wishnewjam.dubstepfm.ui.home.HomeViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// TODOLIST
// если быстро жать плей/стоп, то будет кака

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    @Inject
    lateinit var radioStreamRepositoryImpl: RadioStreamRepositoryImpl

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null
    private val homeViewModel: HomeViewModelImpl by viewModels()

//    private val controllerCallback: MediaControllerCompat.Callback =
//        object : MediaControllerCompat.Callback() {
//            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
//                super.onPlaybackStateChanged(state)
//                logDebug { "controllerCallback: onPlaybackStateChanged, state= ${state?.state}" }
//                homeViewModel.playbackStateChanged(state)
//            }
//
//            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
//                super.onMetadataChanged(metadata)
//                applyMetadata(metadata)
//            }
//        }

//    private val connectionCallback: MediaBrowserCompat.ConnectionCallback =
//        object : MediaBrowserCompat.ConnectionCallback() {
//            override fun onConnected() {
//                super.onConnected()
//                logDebug { "ConnectionCallback: onConnected" }
//                val token: MediaSessionCompat.Token? = mediaBrowser?.sessionToken
//                token?.let {
//                    val controller = MediaControllerCompat(
//                        this@MainActivity,
//                        it
//                    )
//                    MediaControllerCompat.setMediaController(
//                        this@MainActivity,
//                        controller
//                    )
//                    buildTransportControls()
//                    homeViewModel.playbackStateChanged(controller.playbackState)
//                    applyMetadata(controller.metadata)
//                }
//                volumeControlStream = AudioManager.STREAM_MUSIC
//                startService(
//                    Intent(
//                        this@MainActivity,
//                        MainService::class.java
//                    )
//                )
//            }
//
//            override fun onConnectionSuspended() {
//                super.onConnectionSuspended()
//                logDebug { "ConnectionCallback: onConnectionSuspended" }
//            }
//
//            override fun onConnectionFailed() {
//                super.onConnectionFailed()
//                logDebug { "ConnectionCallback: onConnectionFailed" }
//            }
//
//        }

//    private fun applyMetadata(metadata: MediaMetadataCompat?) {
//        lifecycleScope
//        val artist = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
//        val track = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
//        if (artist != null && track != null) {
//            homeViewModel.nowPlayingTextChanged(track)
//        }
//    }

//    private fun buildTransportControls() {
//        val mediaController = MediaControllerCompat.getMediaController(this)
//        mediaController.registerCallback(controllerCallback)
//
//        homeViewModel.userIntentPlayState.observe(this) {
//            if (it == null) return@observe
//            if (it) {
//                mediaController?.transportControls?.play()
//            } else {
//                mediaController?.transportControls?.pause()
//            }
//        }
//
//        lifecycleScope.launch {
//            radioStreamRepo.radioStream
//                .collect { value ->
//                    mediaController?.transportControls?.playFromUri(value.uri, null)
//                }
//        }
//    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DubstepApp(homeViewModel)
        }

        browserFuture =
            MediaBrowser.Builder(
                this,
                SessionToken(this, ComponentName(this, MainService::class.java))
            )
                .buildAsync()

        controller?.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    Log.d(TAG, "onMediaItemTransition() called with: mediaItem = $mediaItem, reason = $reason")

//                    updateMediaMetadataUI(mediaItem?.mediaMetadata ?: MediaMetadata.EMPTY)
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    Log.d(TAG, "onMediaMetadataChanged() called with: mediaMetadata = $mediaMetadata")
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error) // TODO: 03.11.2021
//                    mediaPlayer.stop()
//                    Tools.logDebug { "exoPlayer: onPlayerError: error = ${error.message}" }
//                    notifyStatusChanged(PlayerState.Error(errorText = "${error.message}"))
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Log.d(TAG, "onPlaybackStateChanged() called with: playbackState = $playbackState")
                    when (playbackState) {
//                        Player.STATE_BUFFERING -> notifyStatusChanged(PlayerState.Buffering)
//                        Player.STATE_READY -> {
//                            val state =
//                                if (playWhenReady) {
//                                    PlayerState.Play
//                                } else {
//                                    PlayerState.Pause
//                                }
//                            notifyStatusChanged(state)
//                        }
//                        Player.STATE_IDLE, Player.STATE_ENDED -> {
//                        }
                    }
                }
            }
        )
//        browserFuture.addListener({ pushRoot() }, MoreExecutors.directExecutor())
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        MediaController.releaseFuture(controllerFuture)
    }
}