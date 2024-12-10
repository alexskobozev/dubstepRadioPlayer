package com.wishnewjam.dubstepfm

import android.media.AudioManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player

class MainActivity : AppCompatActivity(), MediaBrowserManager.Callback {

    private lateinit var mediaViewModel: MediaViewModel
    private var mediaBrowserManager: MediaBrowserManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaViewModel = ViewModelProvider(this).get(MediaViewModel::class.java)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupUIControls()

        // Initialize MediaBrowserManager
        mediaBrowserManager = MediaBrowserManager(this, this).apply {
            initialize()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaBrowserManager?.release()
        mediaBrowserManager = null
    }

    override fun onConnected() {
        Tools.logDebug { "MediaBrowser connected." }
        volumeControlStream = AudioManager.STREAM_MUSIC
        mediaBrowserManager?.startService()
        applyCurrentStateAndMetadata()
    }

    override fun onPlaybackStateChanged(isPlaying: Boolean, playbackState: Int) {
        Tools.logDebug { "onPlaybackStateChanged: isPlaying=$isPlaying, state=$playbackState" }
        when {
            playbackState == Player.STATE_BUFFERING -> applyPlaybackState(UIStates.STATUS_LOADING)
            isPlaying -> applyPlaybackState(UIStates.STATUS_PLAY)
            playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE ->
                applyPlaybackState(UIStates.STATUS_STOP)

            else -> applyPlaybackState(UIStates.STATUS_STOP)
        }
    }

    override fun onMediaMetadataChanged(metadata: MediaMetadata) {
        applyMetadata(metadata)
    }

    override fun onError(error: Throwable) {
        Tools.logDebug { "onError: $error" }
        applyPlaybackState(UIStates.STATUS_ERROR)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bitrate -> {
                showBitrateChooser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyCurrentStateAndMetadata() {
        val mgr = mediaBrowserManager ?: return
        val playbackState = mgr.getPlaybackState()
        val isPlaying = mgr.isPlaying()

        when {
            playbackState == Player.STATE_BUFFERING -> applyPlaybackState(UIStates.STATUS_LOADING)
            isPlaying -> applyPlaybackState(UIStates.STATUS_PLAY)
            else -> applyPlaybackState(UIStates.STATUS_STOP)
        }

        mgr.getCurrentMetadata()?.let { applyMetadata(it) }
    }

    private fun applyMetadata(metadata: MediaMetadata) {
        val artist = metadata.artist
        val title = metadata.title
        if (!artist.isNullOrEmpty() && !title.isNullOrEmpty()) {
            val nowPlayingText = "${getString(R.string.now_playing)} $title"
            findViewById<TextView>(R.id.tv_nowplaying).text = nowPlayingText
        }
    }

    private fun applyPlaybackState(state: Int) {
        when (state) {
            UIStates.STATUS_PLAY -> showPlaying()
            UIStates.STATUS_LOADING -> showLoading()
            UIStates.STATUS_ERROR -> showError()
            else -> showStopped()
        }
    }

    private fun setupUIControls() {
        val playButton: Button? = findViewById(R.id.tv_play)
        val stopButton: Button? = findViewById(R.id.tv_stop)

        playButton?.setOnClickListener {
            val mgr = mediaBrowserManager ?: return@setOnClickListener
            if (!mgr.isPlaying()) {
                findViewById<TextView>(R.id.tv_nowplaying).setText(R.string.gathering_info)
            }
            mgr.play()
        }

        stopButton?.setOnClickListener {
            mediaBrowserManager?.stop()
        }
    }

    private fun showLoading() {
        findViewById<LinearLayout>(R.id.ll_loading).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.iv_status).visibility = View.INVISIBLE
    }

    private fun showStopped() {
        findViewById<LinearLayout>(R.id.ll_loading).visibility = View.GONE
        findViewById<ImageView>(R.id.iv_status).setImageResource(R.drawable.ic_stop)
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
        findViewById<ImageView>(R.id.iv_status).visibility = View.VISIBLE
    }

    private fun showPlaying() {
        findViewById<LinearLayout>(R.id.ll_loading).visibility = View.GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
        findViewById<ImageView>(R.id.iv_status).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.iv_status).setImageResource(R.drawable.ic_play)
    }

    private fun showError() {
        findViewById<LinearLayout>(R.id.ll_loading).visibility = View.GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
        findViewById<ImageView>(R.id.iv_status).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.iv_status).setImageResource(R.drawable.ic_stop)
        Tools.toastDebug({ getString(R.string.error) }, this)
    }

    private fun showBitrateChooser() {
        val bitrateFragment = ChooseBitrateDialogFragment()
        bitrateFragment.show(supportFragmentManager, "bitrate")
    }
}
