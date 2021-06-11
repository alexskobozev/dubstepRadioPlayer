package com.wishnewjam.dubstepfm

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.wishnewjam.dubstepfm.Tools.logDebug
import com.wishnewjam.dubstepfm.Tools.toastDebug
import com.wishnewjam.dubstepfm.databinding.ActivityMainBinding
import com.wishnewjam.dubstepfm.databinding.DialogConsentBinding
import com.wishnewjam.dubstepfm.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var mediaBrowser: MediaBrowserCompat? = null
    private lateinit var mediaViewModel: MediaViewModel
    private lateinit var binding: ActivityMainBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private val controllerCallback: MediaControllerCompat.Callback =
            object : MediaControllerCompat.Callback() {
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
                        applyPlaybackState(controller.playbackState.state)
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
            val nowPlayingText = "${getString(R.string.now_playing)} $track"
            binding.includeInfo.tvNowplaying.text = nowPlayingText
        }
    }

    private fun buildTransportControls() {
        val playButton: Button? = findViewById(R.id.tv_play)
        val stopButton: Button? = findViewById(R.id.tv_stop)
        val mediaController = MediaControllerCompat.getMediaController(this)

        playButton?.setOnClickListener {
            if (mediaController.playbackState.state != PlaybackStateCompat.STATE_PLAYING) binding.includeInfo.tvNowplaying.setText(R.string.gathering_info)
            mediaController.transportControls.play()
        }

        stopButton?.setOnClickListener {
            mediaController.transportControls.stop()
        }

        mediaController.registerCallback(controllerCallback)
    }

    private val navigationViewModel by viewModels<NavigationViewModel>()

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as MyApplication).container

        setContent {
            DubstepApp(appContainer,
                    navigationViewModel,
                    homeViewModel)
        }
    }

    override fun onBackPressed() {
        if (!navigationViewModel.onBack()) {
            super.onBackPressed()
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        mediaViewModel = ViewModelProvider(this).get(MediaViewModel::class.java)
//
//        val appContainer = (application as MyApplication).container
//        setContent {
//            DubstepApp(appContainer, navigationViewModel)
//        }
//
////        mediaViewModel.userConsent.observe(this, { t ->
////            t?.let {
////                if (it) {
////                    FirebaseCrashlytics.getInstance()
////                            .setCrashlyticsCollectionEnabled(true)
////                }
////            }
////        })
////        setContentView(binding.rootView)
////
////        if (!mediaViewModel.consentDialogShown) {
////            showConsentDialog()
////        }
////
////        mediaBrowser = MediaBrowserCompat(this,
////                ComponentName(this, MainService::class.java),
////                connectionCallback, null)
//    }

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

    private fun applyPlaybackState(state: Int?) {
        when (state) {
            PlaybackStateCompat.STATE_PLAYING   -> showPlaying()
            PlaybackStateCompat.STATE_BUFFERING -> showLoading()
            PlaybackStateCompat.STATE_ERROR     -> showError()
            else                                -> showStopped()
        }
    }

    private fun showLoading() {
        binding.includeInfo.progressBar.visibility = View.VISIBLE
        binding.includeInfo.ivStatus.visibility = View.INVISIBLE
    }

    private fun showStopped() {
        binding.includeInfo.ivStatus.setImageResource(R.drawable.ic_stop)
        binding.includeInfo.progressBar.visibility = View.INVISIBLE
        binding.includeInfo.ivStatus.visibility = View.VISIBLE
    }

    private fun showPlaying() {
        binding.includeInfo.progressBar.visibility = View.INVISIBLE
        binding.includeInfo.ivStatus.visibility = View.VISIBLE
        binding.includeInfo.ivStatus.setImageResource(R.drawable.ic_play)
    }

    private fun showError() {
        binding.includeInfo.progressBar.visibility = View.INVISIBLE
        binding.includeInfo.ivStatus.visibility = View.VISIBLE
        binding.includeInfo.ivStatus.setImageResource(R.drawable.ic_stop)
        toastDebug({ getString(R.string.error) },
                this)
    }

    private fun showBitrateChooser() {
//        val bitrateFragment = ChooseBitrateDialogFragment()
//        bitrateFragment.show(supportFragmentManager, "bitrate")
    }

    private fun showConsentDialog() {
        val consentBinding = DialogConsentBinding.inflate(layoutInflater,
                binding.rootView,
                false)
        consentBinding.tvConsent.movementMethod = LinkMovementMethod.getInstance()

        MaterialDialog(this).cancelable(false)
                .title(res = R.string.question_to_consent)
                .customView(view = consentBinding.root)
                .positiveButton(R.string.agree) {
                    it.dismiss()
                    mediaViewModel.changeConsent(true)
                }
                .negativeButton(R.string.disagree) {
                    it.dismiss()
                    mediaViewModel.changeConsent(false)
                }
                .onDismiss { mediaViewModel.setDialogShown() }
                .show()
    }
}