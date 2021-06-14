package com.wishnewjam.dubstepfm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.wishnewjam.dubstepfm.Tools.logDebug
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainService : MediaBrowserServiceCompat() {

    companion object {
        private const val NOTIFICATION_ID = 43432
        const val SP_KEY_BITRATE = "link"
        const val SP_KEY_CONSENT = "consent"
        const val SP_KEY_CONSENT_DIALOG_SHOWN = "consent_dialog_shown"
        const val MAX_ERROR_ATTEMPTS = 10

        const val NOTIFICATION_STATUS_PLAY = 1
        const val NOTIFICATION_STATUS_STOP = 0
        const val NOTIFICATION_STATUS_CONNECTING = 2
        const val NOTIFICATION_STATUS_LOADING = 3
        const val NOTIFICATION_STATUS_ERROR = 4
    }

    @Inject
    lateinit var mediaPlayerInstance: DubstepMediaPlayer

    private var mediaSession: MediaSessionCompat? = null
    private var notificationStatus = NOTIFICATION_STATUS_STOP
    private var errorPlayAttempts: Int = 0

    override fun onLoadChildren(parentId: String,
                                result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): BrowserRoot {
        return BrowserRoot("",
                null)
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this,
                "PlayerService")
        mediaPlayerInstance.serviceCallback = mediaPlayerCallback
        mediaSession?.let {
            it.setPlaybackState(PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED,
                            0,
                            0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                    .build())
            val stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
            it.setPlaybackState(stateBuilder.build())

            val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
            val mediaPendingIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext,
                    0,
                    mediaButtonIntent,
                    0)
            it.setMediaButtonReceiver(mediaPendingIntent)
            it.setCallback(Callback())
            it.setSessionActivity(PendingIntent.getActivity(applicationContext,
                    0,
                    Intent(applicationContext,
                            MainActivity::class.java),
                    0))
            sessionToken = it.sessionToken
            it.isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?,
                                flags: Int,
                                startId: Int): Int {
        if (intent == null) stopSelf()
        MediaButtonReceiver.handleIntent(mediaSession,
                intent)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaSession?.release()
        }
        catch (e: Exception) {
            logDebug { "Exception onDestroy: ${e.message}" }
        }
    }

    private val mediaPlayerCallback: MediaPlayerInstance.CallbackInterface
        get() = object : MediaPlayerInstance.CallbackInterface {
            override fun onMetaDataTrackChange(trackName: String) {
                val builder = MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                                "")
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE,
                                trackName)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                10000)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    //sometimes it crashes in android 5.0
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = BitmapFactory.decodeResource(resources,
                                R.drawable.logo_screen)
                    }
                    catch (e: Exception) {
                        logDebug { "exception while decoding logo img" }
                    }
                    bitmap?.let {
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                bitmap)
                    }
                }
                val metadataCompat = builder.build()
                mediaSession?.setMetadata(metadataCompat)
                buildNotification(notificationStatus)
            }

            override fun onChangeStatus(status: Int) {
                logDebug { "mediaPlayerCallback: onChangeStatus, status = $status" }
                when (status) {
                    UIStates.STATUS_PLAY                        -> {
                        errorPlayAttempts = 0
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_PLAYING,
                                        0,
                                        0.0f)
                                .setActions(PlaybackStateCompat.ACTION_STOP)
                                .build())
                        buildNotification(NOTIFICATION_STATUS_PLAY)
                    }
                    UIStates.STATUS_LOADING                     -> {
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_BUFFERING,
                                        0,
                                        0.0f)
                                .setActions(PlaybackStateCompat.ACTION_STOP)
                                .build())
                        buildNotification(NOTIFICATION_STATUS_LOADING)
                    }
                    UIStates.STATUS_STOP, UIStates.STATUS_ERROR -> {
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_STOPPED,
                                        0,
                                        0.0f)
                                .setActions(PlaybackStateCompat.ACTION_PLAY)
                                .build())
                        buildNotification(NOTIFICATION_STATUS_STOP)
                    }
                }
            }

            override fun onError(error: String) {
                mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_ERROR,
                                0,
                                0.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY)
                        .setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR,
                                error)
                        .build())
                buildNotification(NOTIFICATION_STATUS_ERROR)
            }
        }

    private fun buildNotification(keyCode: Int?) {
        val controller = mediaSession?.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description
        val activity = controller?.sessionActivity
        notificationStatus = keyCode ?: NOTIFICATION_STATUS_STOP

        val mBuilder = NotificationCompat.Builder(applicationContext,
                "default")
                .apply {
                    setContentTitle(description?.title)
                    setContentText(description?.subtitle)
                    setSubText(description?.description)
                    setContentIntent(activity)
                    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    setSmallIcon(R.drawable.ic_notification)
                    color = Color.BLACK
                    setDeleteIntent(getDeleteIntent())
                    setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0)
                            .setMediaSession(mediaSession?.sessionToken)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(getDeleteIntent()))
                }

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initChannels(mNotifyMgr)
        when (keyCode) {
            NOTIFICATION_STATUS_PLAY       -> {
                mBuilder.addAction(R.drawable.ic_stop,
                        getString(R.string.stop),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                                PlaybackStateCompat.ACTION_STOP))
                startForeground(NOTIFICATION_ID,
                        mBuilder.build())
            }
            NOTIFICATION_STATUS_CONNECTING -> {
                mBuilder.setContentTitle(getString(R.string.connecting))
                mBuilder.setContentText("")
                mBuilder.addAction(R.drawable.ic_stop,
                        getString(R.string.stop),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                                PlaybackStateCompat.ACTION_STOP))
                startForeground(NOTIFICATION_ID,
                        mBuilder.build())
            }
            NOTIFICATION_STATUS_STOP       -> {
                mBuilder.addAction(R.drawable.ic_play,
                        getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                                PlaybackStateCompat.ACTION_PLAY))
                mNotifyMgr.notify(NOTIFICATION_ID,
                        mBuilder.build())
                stopForeground(false)
            }
            NOTIFICATION_STATUS_LOADING    -> {
                mBuilder.setContentTitle(getString(R.string.loading))
                mBuilder.setContentText("")
                mBuilder.addAction(R.drawable.ic_stop,
                        getString(R.string.stop),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                                PlaybackStateCompat.ACTION_STOP))
                startForeground(NOTIFICATION_ID,
                        mBuilder.build())
            }
            NOTIFICATION_STATUS_ERROR      -> {
                mBuilder.addAction(R.drawable.ic_play,
                        getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                                PlaybackStateCompat.ACTION_PLAY))
                mNotifyMgr.notify(NOTIFICATION_ID,
                        mBuilder.build())
                stopForeground(false)

            }
            else                           -> {
                mBuilder.addAction(R.drawable.ic_play,
                        getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                                PlaybackStateCompat.ACTION_PLAY))
                mNotifyMgr.notify(NOTIFICATION_ID,
                        mBuilder.build())
                stopForeground(false)
            }
        }
    }

    private fun getDeleteIntent(): PendingIntent? {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext,
                PlaybackStateCompat.ACTION_STOP)
    }

    private fun initChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel("default",
                "DUBSTEP.FM",
                NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun handleMediaButtonIntent(event: KeyEvent?): Boolean {
        if (event != null) {
            val action = event.action
            if (action == KeyEvent.ACTION_DOWN) {
                var status: Int? = null
                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                    status = NOTIFICATION_STATUS_STOP
                    mediaPlayerInstance.callStop()
                }
                else if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    status = NOTIFICATION_STATUS_PLAY
                    mediaPlayerInstance.callPlay()
                }
                else if (event.keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                    if (mediaPlayerInstance.status == UIStates.STATUS_PLAY) {
                        status = NOTIFICATION_STATUS_STOP
                        mediaPlayerInstance.callStop()
                    }
                    else {
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

    inner class Callback : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            logDebug { "mediaSessionCallback: onMediaButtonEvent $mediaButtonEvent extras ${mediaButtonEvent?.extras}" }
            val intentAction = mediaButtonEvent?.action
            if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
                return false
            }
            val event: KeyEvent? = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
            return handleMediaButtonIntent(event)
        }

        override fun onCommand(command: String?,
                               extras: Bundle?,
                               cb: ResultReceiver?) {
            logDebug { "mediaSessionCallback: onCommand $command" }
            super.onCommand(command,
                    extras,
                    cb)
        }

        override fun onStop() {
            super.onStop()
            logDebug { "mediaSessionCallback: onStop" }
            mediaPlayerInstance.callStop()
            buildNotification(NOTIFICATION_STATUS_STOP)
        }

        override fun onPlay() {
            super.onPlay()
            logDebug { "mediaSessionCallback: onPlay" }
            mediaPlayerInstance.callPlay()
            buildNotification(NOTIFICATION_STATUS_PLAY)
        }

        override fun onPause() {
            super.onPause()
            logDebug { "mediaSessionCallback: onPause" }
        }
    }
}
