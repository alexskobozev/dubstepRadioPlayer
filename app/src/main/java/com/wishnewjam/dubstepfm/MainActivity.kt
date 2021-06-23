package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wishnewjam.dubstepfm.legacy.Tools.logDebug
import com.wishnewjam.dubstepfm.playback.MainService
import com.wishnewjam.dubstepfm.ui.DubstepApp
import com.wishnewjam.dubstepfm.ui.home.HomeViewModelImpl
import dagger.hilt.android.AndroidEntryPoint


// TODOLIST

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var mediaBrowser: MediaBrowserCompat? = null

    private val homeViewModel: HomeViewModelImpl by viewModels()

    private val controllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                logDebug { "controllerCallback: onPlaybackStateChanged, state= ${state?.state}" }
                homeViewModel.playbackStateChanged(state)
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                applyMetadata(metadata)
            }
        }

    private val connectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                logDebug { "ConnectionCallback: onConnected" }
                val token: MediaSessionCompat.Token? = mediaBrowser?.sessionToken
                token?.let {
                    val controller = MediaControllerCompat(
                        this@MainActivity,
                        it
                    )
                    MediaControllerCompat.setMediaController(
                        this@MainActivity,
                        controller
                    )
                    buildTransportControls()
                    homeViewModel.playbackStateChanged(controller.playbackState)
                    applyMetadata(controller.metadata)
                }
                volumeControlStream = AudioManager.STREAM_MUSIC
                startService(
                    Intent(
                        this@MainActivity,
                        MainService::class.java
                    )
                )
            }

            override fun onConnectionSuspended() {
                super.onConnectionSuspended()
                logDebug { "ConnectionCallback: onConnectionSuspended" }
            }

            override fun onConnectionFailed() {
                super.onConnectionFailed()
                logDebug { "ConnectionCallback: onConnectionFailed" }
            }

        }

    private fun applyMetadata(metadata: MediaMetadataCompat?) {
        val artist = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        val track = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        if (artist != null && track != null) {
            homeViewModel.nowPlayingTextChanged(track)
        }
    }

    private fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        mediaController.registerCallback(controllerCallback)

        homeViewModel.userIntentPlayState.observe(this) {
            if (it == null) return@observe
            if (it) {
                mediaController?.transportControls?.play()
            } else {
                mediaController?.transportControls?.pause()
            }
        }

        homeViewModel.currentRadioStream.observe(this) {
            mediaController?.transportControls?.playFromUri(it.uri, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DubstepApp(homeViewModel)
        }
        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(
                this,
                MainService::class.java
            ),
            connectionCallback,
            null
        )
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser?.connect()
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)
            ?.unregisterCallback(controllerCallback)
        mediaBrowser?.disconnect()
    }
}