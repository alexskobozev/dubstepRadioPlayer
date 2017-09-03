package com.wishnewjam.dubstepfm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.os.AsyncTask
import android.os.Bundle
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
import java.net.URL
import javax.inject.Inject


class MainService : MediaBrowserServiceCompat() {

    companion object {
        private val NOTIFICATION_ID = 43432
        val SP_KEY_BITRATE = "link"
    }

    val NOTIFICATION_STATUS_PLAY = 1
    val NOTIFICATION_STATUS_STOP = 0

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance
    private var mediaSession: MediaSessionCompat? = null
    private var notificationStatus = NOTIFICATION_STATUS_STOP

    private var mMediaButtonReceiver: MediaButtonIntentReceiver? = null

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        if (TextUtils.equals(clientPackageName, packageName)) {
            return BrowserRoot(getString(R.string.app_name), null)
        } else {
            return MediaBrowserServiceCompat.BrowserRoot("", null)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        MyApplication.graph.inject(this)
        val receiver = ComponentName(packageName, MediaButtonIntentReceiver::class.java.name)
        mediaSession = MediaSessionCompat(this, "PlayerService", receiver, null)
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

            val mediaButtonIntent: Intent = Intent(Intent.ACTION_MEDIA_BUTTON)
            mediaButtonIntent.setClass(applicationContext, MediaButtonIntentReceiver::class.java)
            val mediaPendingIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0)
            it.setMediaButtonReceiver(mediaPendingIntent)
            it.setCallback(object : MediaSessionCompat.Callback() {
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    logDebug({ "mediaSessionCallback: onMediaButtonEvent $mediaButtonEvent" })
                    return handleMediaButtonIntent(mediaButtonIntent)
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
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) stopSelf()
        initHeadsetReceiver()
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        registerReceiver(mNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaButtonReceiver?.let { unregisterReceiver(it) }
        unregisterReceiver(mNoisyReceiver)
        mediaSession?.release()
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
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0.0f)
                                .setActions(PlaybackStateCompat.ACTION_STOP).build())
                        GetMetadataAsyncTask().execute()
                    }
                    UIStates.STATUS_LOADING -> {
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_BUFFERING, 0, 0.0f)
                                .setActions(PlaybackStateCompat.ACTION_STOP).build())
                    }
                    UIStates.STATUS_STOP, UIStates.STATUS_ERROR -> {
                        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_STOPPED, 0, 0.0f)
                                .setActions(PlaybackStateCompat.ACTION_PLAY).build())
                    }
                }
            }

            override fun onError(error: String) {
                mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_ERROR, 0, 0.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY).build())
            }

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
                .setLargeIcon(description?.iconBitmap)
                .setContentIntent(activity)
                .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.BLACK)
                .setStyle(NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                        .setMediaSession(mediaSession?.sessionToken)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP)))


        if (keyCode == NOTIFICATION_STATUS_PLAY) {
            mBuilder.addAction(R.drawable.ic_stop, getString(R.string.stop),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP))
            startForeground(NOTIFICATION_ID, mBuilder.build())
        } else {
            mBuilder.addAction(R.drawable.ic_play, getString(R.string.play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY))
            val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build())
            stopForeground(false)
        }
    }

    private fun handleMediaButtonIntent(intent: Intent): Boolean {
        val intentAction = intent.action
        if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
            return false
        }
        val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return false
        val action = event.action
        if (action == KeyEvent.ACTION_DOWN) {
            var status: Int? = null
            if (event.keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                status = NOTIFICATION_STATUS_STOP
                mediaPlayerInstance.callStop()
                return true
            } else if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                status = NOTIFICATION_STATUS_PLAY
                mediaPlayerInstance.callPlay()
                return true
            }
            buildNotification(status)
        }
        return false
    }

    //TODO: check if we don't need that receiver in api below 21
    inner class MediaButtonIntentReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            handleMediaButtonIntent(intent)
        }
    }

    inner class MetaData(var artist: String?, var title: String?)

    inner class GetMetadataAsyncTask : AsyncTask<Void, Void, MetaData>() {
        override fun doInBackground(vararg p0: Void?): MetaData {
            val url = URL(Links.LINK_128)
            val meta: IcyStreamMeta = IcyStreamMeta(url)
            meta.refreshMeta()

            return MetaData(meta.artist, meta.title)
        }

        override fun onPostExecute(result: MetaData?) {
            super.onPostExecute(result)
            mediaSession?.setMetadata(MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, result?.artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, result?.title)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                            BitmapFactory.decodeResource(resources, R.drawable.logo_screen))
                    .build())
            buildNotification(notificationStatus)
        }
    }
}
