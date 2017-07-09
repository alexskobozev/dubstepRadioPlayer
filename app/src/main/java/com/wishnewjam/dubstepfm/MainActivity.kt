package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.wishnewjam.dubstepfm.Tools.logDebug
import io.fabric.sdk.android.Fabric


class MainActivity : AppCompatActivity() {

    private var loadingIndicator: View? = null
    private var mediaBrowser: MediaBrowserCompat? = null
    private var statusIcon: ImageView? = null
    private var loadingIndicatorSmall: ProgressBar? = null
    private var nowPlayingTextView: TextView? = null

    private val controllerCallback: MediaControllerCompat.Callback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            logDebug({ "controllerCallback: onPlaybackStateChanged, state= ${state?.state}" })
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
            nowPlayingTextView?.text = nowPlayingText
        }
    }

    private fun buildTransportControls() {
        val playButton: Button? = findViewById(R.id.tv_play)
        val stopButton: Button? = findViewById(R.id.tv_stop)
        val mediaController = MediaControllerCompat.getMediaController(this)

        playButton?.setOnClickListener({
            mediaController.transportControls.play()
        })

        stopButton?.setOnClickListener({
            mediaController.transportControls.stop()
        })

        mediaController.registerCallback(controllerCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingIndicator = findViewById(R.id.ll_loading)
        statusIcon = findViewById(R.id.iv_status)
        loadingIndicatorSmall = findViewById(R.id.progressBar)
        nowPlayingTextView = findViewById(R.id.tv_nowplaying)
        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, MainService::class.java), connectionCallback, null)
        Fabric.with(this, Crashlytics())
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser?.connect()
    }

    override fun onPause() {
        super.onPause()
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
        when (item.itemId) {
            R.id.action_bitrate -> {
                showBitrateChooser()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun applyPlaybackState(state: Int?) {
        when (state) {
            PlaybackStateCompat.STATE_PLAYING -> showPlaying()
            PlaybackStateCompat.STATE_BUFFERING -> showLoading()
            PlaybackStateCompat.STATE_ERROR -> showError()
            else -> showStopped()
        }
    }

    private fun showLoading() {
        loadingIndicator?.visibility = View.VISIBLE
        loadingIndicatorSmall?.visibility = View.VISIBLE
        statusIcon?.visibility = View.INVISIBLE
    }

    private fun showStopped() {
        loadingIndicator?.visibility = View.GONE
        statusIcon?.setImageResource(R.drawable.ic_stop)
        loadingIndicatorSmall?.visibility = View.INVISIBLE
        statusIcon?.visibility = View.VISIBLE
    }

    private fun showPlaying() {
        loadingIndicator?.visibility = View.GONE
        loadingIndicatorSmall?.visibility = View.INVISIBLE
        statusIcon?.visibility = View.VISIBLE
        statusIcon?.setImageResource(R.drawable.ic_play)
    }

    private fun showError() {
        loadingIndicator?.visibility = View.GONE
        loadingIndicatorSmall?.visibility = View.INVISIBLE
        statusIcon?.visibility = View.VISIBLE
        statusIcon?.setImageResource(R.drawable.ic_stop)
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
    }

    private fun showBitrateChooser() {
        val bitrateFragment = ChooseBitrateDialogFragment()
        bitrateFragment.show(supportFragmentManager, "bitrate")
    }
}