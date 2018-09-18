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
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.wishnewjam.dubstepfm.Tools.logDebug
import com.wishnewjam.dubstepfm.Tools.toastDebug
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.info_layout.*
import kotlinx.android.synthetic.main.loading_layout.*

class MainActivity : AppCompatActivity() {

    private var mediaBrowser: MediaBrowserCompat? = null

    private val controllerCallback: MediaControllerCompat.Callback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            logDebug { "controllerCallback: onPlaybackStateChanged, state= ${state?.state}" }
            applyPlaybackState(state?.state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            applyMetadata(metadata)
        }
    }

    private val connectionCallback: MediaBrowserCompat.ConnectionCallback? = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            logDebug { "ConnectionCallback: onConnected" }
            val token: MediaSessionCompat.Token? = mediaBrowser?.sessionToken
            token?.let {
                val controller = MediaControllerCompat(this@MainActivity, it)
                MediaControllerCompat.setMediaController(this@MainActivity, controller)
                buildTransportControls()
                applyPlaybackState(controller.playbackState.state)
                applyMetadata(controller.metadata)
            }
            volumeControlStream = AudioManager.STREAM_MUSIC
            startService(Intent(this@MainActivity, MainService::class.java))
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
            val nowPlayingText = "${getString(R.string.now_playing)} $artist - $track"
            tv_nowplaying.text = nowPlayingText
        }
    }

    private fun buildTransportControls() {
        val playButton: Button? = findViewById(R.id.tv_play)
        val stopButton: Button? = findViewById(R.id.tv_stop)
        val mediaController = MediaControllerCompat.getMediaController(this)

        playButton?.setOnClickListener {
            if (mediaController.playbackState.state != PlaybackStateCompat.STATE_PLAYING) tv_nowplaying.setText(R.string.gathering_info)
            mediaController.transportControls.play()
        }

        stopButton?.setOnClickListener {
            mediaController.transportControls.stop()
        }

        mediaController.registerCallback(controllerCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fabric = Fabric.Builder(this).kits(Crashlytics())
//                .debuggable(true)  // Enables Crashlytics debugger
                .build()
        Fabric.with(fabric)
        setContentView(R.layout.activity_main)
        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, MainService::class.java), connectionCallback, null)
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser?.connect()
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mediaBrowser?.disconnect()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
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

    private fun applyPlaybackState(state: Int?) {
        when (state) {
            PlaybackStateCompat.STATE_PLAYING   -> showPlaying()
            PlaybackStateCompat.STATE_BUFFERING -> showLoading()
            PlaybackStateCompat.STATE_ERROR     -> showError()
            else                                -> showStopped()
        }
    }

    private fun showLoading() {
        ll_loading.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        iv_status.visibility = View.INVISIBLE
    }

    private fun showStopped() {
        ll_loading.visibility = View.GONE
        iv_status.setImageResource(R.drawable.ic_stop)
        progressBar.visibility = View.INVISIBLE
        iv_status.visibility = View.VISIBLE
    }

    private fun showPlaying() {
        ll_loading.visibility = View.GONE
        progressBar.visibility = View.INVISIBLE
        iv_status.visibility = View.VISIBLE
        iv_status.setImageResource(R.drawable.ic_play)
    }

    private fun showError() {
        ll_loading.visibility = View.GONE
        progressBar.visibility = View.INVISIBLE
        iv_status.visibility = View.VISIBLE
        iv_status.setImageResource(R.drawable.ic_stop)
        toastDebug({ getString(R.string.error) }, this)
    }

    private fun showBitrateChooser() {
        val bitrateFragment = ChooseBitrateDialogFragment()
        bitrateFragment.show(supportFragmentManager, "bitrate")
    }
}