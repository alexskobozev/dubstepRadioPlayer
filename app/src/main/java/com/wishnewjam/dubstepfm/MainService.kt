package com.wishnewjam.dubstepfm

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.KeyEvent

import java.io.IOException

class MainService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private var mMediaPlayer: MediaPlayer? = null
    private var wifiLock: WifiManager.WifiLock? = null
    private var currentLink: String? = null
    var status = Statuses.IDLE
        private set
    private var audioManager: AudioManager? = null
    private var mMediaButtonReceiver: MediaButtonIntentReceiver? = null

    override fun onCompletion(mp: MediaPlayer) {
        retryHandler?.postDelayed(retryRunnable, TIME_TO_RETRY)
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        sendMessage(ERROR)
        stopSelf()
        return false
    }

    enum class Statuses {
        PLAYING, WAITING, IDLE
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        retryHandler = Handler()
        try {
            initMediaPlayer()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Service.START_STICKY
    }


    @Throws(IOException::class)
    private fun initMediaPlayer() {
        if (status == Statuses.WAITING) return
        status = Statuses.WAITING
        sendMessage(WAITING)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val link = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SP_KEY_BITRATE, Links.LINK_256)

        if (mMediaPlayer?.isPlaying ?: false) {
            if (link == currentLink) {
                sendMessage(PLAYING)
                return
            } else {
                mMediaPlayer?.stop()
                mMediaPlayer?.reset()
                mMediaPlayer = null
            }
        }
        initHeadsetReceiver()

        currentLink = link

        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        mMediaPlayer?.setDataSource(link)

        mMediaPlayer?.setOnPreparedListener(this)
        mMediaPlayer?.setOnCompletionListener(this)
        mMediaPlayer?.setOnErrorListener(this)
        mMediaPlayer?.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer?.prepareAsync()

    }

    private fun initHeadsetReceiver() {
        if (mMediaButtonReceiver != null) {
            unregisterReceiver(mMediaButtonReceiver)
        }
        mMediaButtonReceiver = MediaButtonIntentReceiver()
        val mediaFilter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
        mediaFilter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        registerReceiver(mMediaButtonReceiver, mediaFilter)

    }

    private val retryRunnable = Runnable {
        try {
            initMediaPlayer()
        } catch (e: IOException) {
            post(10000)
        }
    }

    private fun post(i: Int) {
        retryHandler?.postDelayed(retryRunnable, i.toLong())
    }

    private fun initWakeLock() {
        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WAKE_LOCK)
        wifiLock?.acquire()
    }

    private fun releaseWakeLock() {
        if (wifiLock != null) {
            wifiLock?.release()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        internal val service: MainService
            get() = this@MainService
    }


    override fun onPrepared(player: MediaPlayer) {

        val result = audioManager?.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player.start()
            initNotification()
            initWakeLock()
            status = Statuses.PLAYING
            sendMessage(PLAYING)
        }

    }

    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (mMediaPlayer != null) mMediaPlayer?.pause()
            AudioManager.AUDIOFOCUS_GAIN -> if (mMediaPlayer != null)
                mMediaPlayer?.start()
            else {
                try {
                    initMediaPlayer()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            AudioManager.AUDIOFOCUS_LOSS -> if (mMediaPlayer != null) {
                if (mMediaPlayer?.isPlaying ?: false) {
                    mMediaPlayer?.stop()
                }
                mMediaPlayer?.reset()
                mMediaPlayer?.release()
                mMediaPlayer = null

            } else {
                stopSelf()
            }
        }


        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            if (mMediaPlayer != null) mMediaPlayer?.pause()
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            try {
                initMediaPlayer()
            } catch (e: IOException) {
                e.printStackTrace()
                stopSelf()
            }

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stopSelf()
        }
    }

    private fun initNotification() {

        val resultIntent = Intent(this, MainActivity::class.java)

        val resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(resultPendingIntent)

        startForeground(NOTIFICATION_ID, mBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaButtonReceiver != null) {
            unregisterReceiver(mMediaButtonReceiver)
        }
        if (audioManager != null) {
            audioManager?.abandonAudioFocus(afChangeListener)
        }
        releaseWakeLock()
        if (mMediaPlayer != null) {
            if (mMediaPlayer?.isPlaying ?: false) {
                mMediaPlayer?.stop()
            }
            mMediaPlayer?.reset()
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
        if (retryHandler != null) {
            retryHandler?.removeCallbacks(retryRunnable)
        }
    }

    private fun sendMessage(message: String) {
        val intent = Intent(MainActivity.ACTION_RECEIVE_AUDIO_INFO)
        intent.putExtra(MainActivity.TAG_MESSAGE, message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    inner class MediaButtonIntentReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val intentAction = intent.action
            if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
                return
            }
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return
            val action = event.action
            if (action == KeyEvent.ACTION_DOWN) {
                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                    if (mMediaPlayer != null) {
                        if (mMediaPlayer?.isPlaying ?: false) {
                            mMediaPlayer?.pause()
                        } else {
                            mMediaPlayer?.prepareAsync()
                        }
                    }
                }

                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer?.prepareAsync()
                    }
                }
            }
            abortBroadcast()

        }
    }

    companion object {
        private val NOTIFICATION_ID = 43432
        private val TIME_TO_RETRY: Long = 3000

        val ACTION_PLAY = "com.example.action.PLAY"
        private val WAKE_LOCK = "mp_wakelock"
        val SP_KEY_BITRATE = "link"

        val PLAYING = "playing"
        val WAITING = "waiting"
        val ERROR = "error"
        private var retryHandler: Handler? = null
    }

}
