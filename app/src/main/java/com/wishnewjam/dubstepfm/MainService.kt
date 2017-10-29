package com.wishnewjam.dubstepfm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.NotificationCompat
import android.text.TextUtils
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.wishnewjam.dubstepfm.Tools.logDebug
import io.fabric.sdk.android.Fabric
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.net.URL
import javax.inject.Inject


class MainService : MediaBrowserServiceCompat() {

    companion object {
        private val NOTIFICATION_ID = 43432
        val SP_KEY_BITRATE = "link"
        val MAX_ERROR_ATTEMPTS = 10
    }

    val NOTIFICATION_STATUS_PLAY = 1
    val NOTIFICATION_STATUS_STOP = 0
    val NOTIFICATION_STATUS_CONNECTING = 2
    val NOTIFICATION_STATUS_LOADING = 3
    val NOTIFICATION_STATUS_ERROR = 4

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance
    private var mediaSession: MediaSessionCompat? = null
    private var notificationStatus = NOTIFICATION_STATUS_STOP
    private var errorPlayAttempts: Int = 0

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            BrowserRoot(getString(R.string.app_name), null)
        } else {
            MediaBrowserServiceCompat.BrowserRoot("", null)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        MyApplication.graph.inject(this)
        mediaSession = MediaSessionCompat(this, "PlayerService")
        mediaPlayerInstance.serviceCallback = mediaPlayerCallback
        mediaSession?.let {
            it.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            it.setPlaybackState(PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                    .build())
            val stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
            it.setPlaybackState(stateBuilder.build())

            val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
            val mediaPendingIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0)
            it.setMediaButtonReceiver(mediaPendingIntent)
            it.setCallback(object : MediaSessionCompat.Callback() {
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    logDebug({ "mediaSessionCallback: onMediaButtonEvent $mediaButtonEvent extras ${mediaButtonEvent?.extras}" })
                    val intentAction = mediaButtonEvent?.action
                    if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
                        return false
                    }
                    val event: KeyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                    return handleMediaButtonIntent(event)
                }

                override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
                    logDebug({ "mediaSessionCallback: onCommand $command" })
                    super.onCommand(command, extras, cb)
                }


                override fun onStop() {
                    super.onStop()
                    logDebug({ "mediaSessionCallback: onStop" })
                    mediaPlayerInstance.callStop()
                    buildNotification(NOTIFICATION_STATUS_STOP)
                }

                override fun onPlay() {
                    super.onPlay()
                    logDebug({ "mediaSessionCallback: onPlay" })
                    mediaPlayerInstance.callPlay()
                    buildNotification(NOTIFICATION_STATUS_PLAY)
                }

                override fun onPause() {
                    super.onPause()
                    logDebug({ "mediaSessionCallback: onPause" })
                }
            })
            it.setSessionActivity(PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), 0))
            sessionToken = it.sessionToken
            it.isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) stopSelf()
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        registerReceiver(mNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mNoisyReceiver)
        } catch (e: Exception) {
            Tools.logDebug { "Exception onDestroy: ${e.message}" }
        }
        try {
            mediaSession?.release()
        } catch (e: Exception) {
            Tools.logDebug { "Exception onDestroy: ${e.message}" }
        }
    }

    private val mNoisyReceiver: BroadcastReceiver
        get() = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                mediaPlayerInstance.callStop()
            }
        }

    private val mediaPlayerCallback: MediaPlayerInstance.CallbackInterface
        get() = object : MediaPlayerInstance.CallbackInterface {
            override fun onChangeStatus(status: Int) {
                logDebug { "mediaPlayerCallback: onChangeStatus, status = $status" }
                when (status) {
                    UIStates.STATUS_PLAY -> {
                        errorPlayAttempts = 0
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0.0f)
                                .setActions(PlaybackStateCompat.ACTION_STOP).build())
                        getMetaData()
                        buildNotification(NOTIFICATION_STATUS_PLAY)
                    }
                    UIStates.STATUS_LOADING -> {
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_BUFFERING, 0, 0.0f)
                                .setActions(PlaybackStateCompat.ACTION_STOP).build())
                        buildNotification(NOTIFICATION_STATUS_LOADING)
                    }
                    UIStates.STATUS_STOP, UIStates.STATUS_ERROR -> {
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_STOPPED, 0, 0.0f)
                                .setActions(PlaybackStateCompat.ACTION_PLAY).build())
                        buildNotification(NOTIFICATION_STATUS_STOP)
                    }
                }
            }

            override fun onError(error: String) {
                if (++errorPlayAttempts < MAX_ERROR_ATTEMPTS) {
                    mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 0.0f)
                            .setActions(PlaybackStateCompat.ACTION_STOP).build())
                    buildNotification(NOTIFICATION_STATUS_CONNECTING)
                    Handler().postDelayed({ mediaPlayerInstance.callPlay() }, 10000)
                } else {
                    mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_ERROR, 0, 0.0f)
                            .setActions(PlaybackStateCompat.ACTION_PLAY).build())
                    buildNotification(NOTIFICATION_STATUS_ERROR)
                }
            }

        }

    private fun buildNotification(keyCode: Int?) {
        val controller = mediaSession?.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description
        val activity = controller?.sessionActivity
        notificationStatus = keyCode ?: NOTIFICATION_STATUS_STOP

        val mBuilder = NotificationCompat.Builder(applicationContext)
        mBuilder
                .setContentTitle(description?.title)
                .setContentText(description?.subtitle)
                .setSubText(description?.description)
                .setContentIntent(activity)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.BLACK)
                .setStyle(NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                        .setMediaSession(mediaSession?.sessionToken)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP)))

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (keyCode) {
            NOTIFICATION_STATUS_PLAY -> {
                mBuilder.addAction(R.drawable.ic_stop, getString(R.string.stop),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP))
                startForeground(NOTIFICATION_ID, mBuilder.build())
            }
            NOTIFICATION_STATUS_CONNECTING -> {
                mBuilder.setContentTitle(getString(R.string.connecting))
                mBuilder.setContentText("")
                mBuilder.addAction(R.drawable.ic_stop, getString(R.string.stop),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP))
                startForeground(NOTIFICATION_ID, mBuilder.build())
            }
            NOTIFICATION_STATUS_STOP -> {
                mBuilder.addAction(R.drawable.ic_play, getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY))
                mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build())
                stopForeground(false)
            }
            NOTIFICATION_STATUS_LOADING -> {
                mBuilder.setContentTitle(getString(R.string.loading))
                mBuilder.setContentText("")
                mBuilder.addAction(R.drawable.ic_stop, getString(R.string.stop),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP))
                startForeground(NOTIFICATION_ID, mBuilder.build())
            }
            NOTIFICATION_STATUS_ERROR -> {
                mBuilder.addAction(R.drawable.ic_play, getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY))
                mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build())
                stopForeground(false)

            }
            else -> {
                mBuilder.addAction(R.drawable.ic_play, getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY))
                mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build())
                stopForeground(false)
            }
        }
    }

    private fun handleMediaButtonIntent(event: KeyEvent?): Boolean {
        if (event != null) {
            val action = event.action
            if (action == KeyEvent.ACTION_DOWN) {
                var status: Int? = null
                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                    status = NOTIFICATION_STATUS_STOP
                    mediaPlayerInstance.callStop()
                } else if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    status = NOTIFICATION_STATUS_PLAY
                    mediaPlayerInstance.callPlay()
                } else if (event.keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                    if (mediaPlayerInstance.status == UIStates.STATUS_PLAY) {
                        status = NOTIFICATION_STATUS_STOP
                        mediaPlayerInstance.callStop()
                    } else {
                        status = NOTIFICATION_STATUS_PLAY
                        mediaPlayerInstance.callPlay()
                    }
                }
                buildNotification(status)
                return true
            }
        }
        return false
    }

    private fun getMetaData() {
        launch(Android) {
            async(CommonPool) {
                val url = URL(Links.LINK_128)
                val meta = IcyStreamMeta(url)
                meta.refreshMeta()
                MetaData(meta.artist, meta.title)
            }.await()
                    .let { metaData ->
                        mediaSession?.setMetadata(MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, metaData.artist)
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, metaData.title)
                                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
                                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                        BitmapFactory.decodeResource(resources, R.drawable.logo_screen))
                                .build())
                        buildNotification(notificationStatus)
                    }
        }
    }

    inner class MetaData(var artist: String?, var title: String?)
}
