package com.wishnewjam.dubstepfm

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.util.Log
import dagger.Module
import java.lang.ref.WeakReference

@Module
class MediaPlayerInstance(context: Context) : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    val TAG = MediaPlayerInstance::class.java.name

    private val WAKE_LOCK = "mp_wakelock"
    var status: Int = UIStates.STATUS_UNDEFINED
    var activityCallback: WeakReference<CallbackInterface>? = null
    var serviceCallback: WeakReference<CallbackInterface>? = null


    private var audioManager: AudioManager? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var currentUrl: CurrentUrl = CurrentUrl(context)

    private var wifiLock: WifiManager.WifiLock?

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)

        wifiLock = (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WAKE_LOCK)
    }


    private val afChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener {
        focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                notifyStatusChanged(UIStates.STATUS_WAITING)
            }
            AudioManager.AUDIOFOCUS_GAIN -> if (status == UIStates.STATUS_WAITING) {
                mediaPlayer.start()
                notifyStatusChanged(UIStates.STATUS_PLAY)
            }
        }
    }

    private fun initWakeLock() {

        wifiLock?.acquire()
    }

    private fun releaseWakeLock() {
        if (wifiLock != null) {
            wifiLock?.release()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.start()
        notifyStatusChanged(UIStates.STATUS_PLAY)
    }

    private fun notifyStatusChanged(status: Int) {
        this.status = status
        activityCallback?.get()?.onChangeStatus(status)
        serviceCallback?.get()?.onChangeStatus(status)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        notifyStatusChanged(UIStates.STATUS_STOP)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        status = UIStates.STATUS_ERROR
        activityCallback?.get()?.onError("Playback error: $what, extra: $extra")
        serviceCallback?.get()?.onError("Playback error: $what, extra: $extra")
        return true
    }

    interface CallbackInterface {
        fun onChangeStatus(status: Int)
        fun onError(error: String)
    }

    fun callPlay() {
        Log.d(TAG, "Call play, status: $status")
        when (status) {
            UIStates.STATUS_STOP -> {
                mediaPlayer.prepareAsync()
                notifyStatusChanged(UIStates.STATUS_LOADING)
                audioManager?.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            }

            UIStates.STATUS_PLAY -> {
                // do nothing
            }

            UIStates.STATUS_LOADING -> {
                // do nothing
            }

            else -> {
                mediaPlayer.setDataSource(currentUrl.currentUrl)
                mediaPlayer.prepareAsync()
                notifyStatusChanged(UIStates.STATUS_LOADING)
                audioManager?.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            }
        }
    }

    fun callStop() {
        when (status) {
            UIStates.STATUS_PLAY -> {
                mediaPlayer.stop()
                notifyStatusChanged(UIStates.STATUS_STOP)
                audioManager?.abandonAudioFocus(afChangeListener)
            }
        }

    }

    fun changeUrl(newUrl: String) {
        if (currentUrl.currentUrl != newUrl) {
            currentUrl.updateUrl(newUrl)
            mediaPlayer.reset()
            val oldStatus = status
            notifyStatusChanged(UIStates.STATUS_UNDEFINED)
            if (oldStatus == UIStates.STATUS_PLAY) {
                callPlay()
            }
        }
    }

    fun callPlayOrPause() {
        if (status == UIStates.STATUS_PLAY) {
            mediaPlayer.stop()
            notifyStatusChanged(UIStates.STATUS_STOP)
        } else {
            callPlay()
        }
    }

}