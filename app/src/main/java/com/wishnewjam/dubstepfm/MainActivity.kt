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
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wishnewjam.dubstepfm.legacy.DubstepApp
import com.wishnewjam.dubstepfm.legacy.NavigationViewModel
import com.wishnewjam.dubstepfm.legacy.Tools.logDebug
import com.wishnewjam.dubstepfm.ui.state.UiState
import com.wishnewjam.dubstepfm.playback.MainService
import com.wishnewjam.dubstepfm.ui.home.HomeViewModelImpl
import dagger.hilt.android.AndroidEntryPoint


// TODOLIST
// audiofocus and controls handling in headphones
// view states when reopen activity after stop/ from notifications
// leaks
// сервис пропадает

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var mediaBrowser: MediaBrowserCompat? = null

    private val homeViewModel: HomeViewModelImpl by viewModels()
    private val navigationViewModel: NavigationViewModel by viewModels()

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

                override fun binderDied() {
                    super.binderDied()
                }

                override fun onSessionReady() {
                    super.onSessionReady()
                }

                override fun onSessionDestroyed() {
                    super.onSessionDestroyed()
                }

                override fun onSessionEvent(event: String?,
                                            extras: Bundle?) {
                    super.onSessionEvent(event,
                            extras)
                }

                override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
                    super.onQueueChanged(queue)
                }

                override fun onQueueTitleChanged(title: CharSequence?) {
                    super.onQueueTitleChanged(title)
                }

                override fun onExtrasChanged(extras: Bundle?) {
                    super.onExtrasChanged(extras)
                }

                override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
                    super.onAudioInfoChanged(info)
                }

                override fun onCaptioningEnabledChanged(enabled: Boolean) {
                    super.onCaptioningEnabledChanged(enabled)
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    super.onRepeatModeChanged(repeatMode)
                }

                override fun onShuffleModeChanged(shuffleMode: Int) {
                    super.onShuffleModeChanged(shuffleMode)
                }
            }

    private val connectionCallback: MediaBrowserCompat.ConnectionCallback =
            object : MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    super.onConnected()
                    logDebug { "ConnectionCallback: onConnected" }
                    val token: MediaSessionCompat.Token? = mediaBrowser?.sessionToken
                    token?.let {
                        val controller = MediaControllerCompat(this@MainActivity,
                                it)
                        MediaControllerCompat.setMediaController(this@MainActivity,
                                controller)
                        buildTransportControls()
                        homeViewModel.playbackStateChanged(controller.playbackState)
                        applyMetadata(controller.metadata)
                    }
                    volumeControlStream = AudioManager.STREAM_MUSIC
                    startService(Intent(this@MainActivity,
                            MainService::class.java))
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
        val playButton: Button? = findViewById(R.id.tv_play)
        val stopButton: Button? = findViewById(R.id.tv_stop)
        val mediaController = MediaControllerCompat.getMediaController(this)

        playButton?.setOnClickListener {
            mediaController.transportControls.play()
        }

        stopButton?.setOnClickListener {
            mediaController.transportControls.stop()
        }

        mediaController.registerCallback(controllerCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DubstepApp(navigationViewModel,
                    homeViewModel)
        }
        homeViewModel.playButtonState.observe(this) {
            when (it) {
                is UiState.Play -> mediaController?.transportControls?.play()
                is UiState.Stop -> mediaController?.transportControls?.stop()
            }
        }

        mediaBrowser = MediaBrowserCompat(this,
                ComponentName(this,
                        MainService::class.java),
                connectionCallback,
                null)

    }

    override fun onBackPressed() {
        if (!navigationViewModel.onBack()) {
            super.onBackPressed()
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main,
                menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bitrate -> {
                showBitrateChooser()
                true
            }
            else                -> super.onOptionsItemSelected(item)
        }
    }

    private fun showBitrateChooser() {
//        val bitrateFragment = ChooseBitrateDialogFragment()
//        bitrateFragment.show(supportFragmentManager, "bitrate")
    }

}