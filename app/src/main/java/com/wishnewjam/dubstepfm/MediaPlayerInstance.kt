package com.wishnewjam.dubstepfm

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager

class MediaPlayerInstance(context: Context) : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    companion object {
        val STATUS_UNDEFINED = 1
        val STATUS_PLAY = 1
        val STATUS_STOP = 2
        val STATUS_ERROR = 3
        val STATUS_LOADING = 4
        val STATUS_DESTROYED = 5
        val STATUS_WAITING = 6
    }

    private val WAKE_LOCK = "mp_wakelock"
    var status: Int = STATUS_UNDEFINED
    var callback: CallbackInterface? = null

    private var audioManager: AudioManager? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()

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
                status = STATUS_WAITING
            }
            AudioManager.AUDIOFOCUS_GAIN -> if (status == STATUS_WAITING) {
                mediaPlayer.start()
            }
        }
    }


    fun play(url: String) {
        status = STATUS_LOADING
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        callback?.onStartPreparing()
        audioManager?.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
    }

    fun stop() {
        status = STATUS_STOP
        mediaPlayer.stop()
        callback?.onStop()
        audioManager?.abandonAudioFocus(afChangeListener)
    }

    fun destroy() {
        status = STATUS_DESTROYED
        mediaPlayer.release()
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
        status = STATUS_PLAY
        callback?.onPlay()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        status = STATUS_STOP
        callback?.onStop()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        status = STATUS_ERROR
        callback?.onError("Playback error: $what, extra: $extra")
        return true
    }

    interface CallbackInterface {
        fun onStartPreparing()
        fun onPlay()
        fun onStop()
        fun onError(error: String)
    }

}