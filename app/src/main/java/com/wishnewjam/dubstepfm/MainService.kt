package com.wishnewjam.dubstepfm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.wishnewjam.dubstepfm.Tools.logDebug

@UnstableApi
class MainService : MediaSessionService() {

    companion object {
        private const val NOTIFICATION_ID = 43432
        const val SP_KEY_BITRATE = "link"
        const val MAX_ERROR_ATTEMPTS = 10

        const val NOTIFICATION_STATUS_PLAY = 1
        const val NOTIFICATION_STATUS_STOP = 0
        const val NOTIFICATION_STATUS_CONNECTING = 2
        const val NOTIFICATION_STATUS_LOADING = 3
        const val NOTIFICATION_STATUS_ERROR = 4
    }

    private lateinit var mediaPlayerInstance: MediaPlayerInstance
    private lateinit var mediaSession: MediaSession
    private val handler = Handler()

    private var notificationStatus = NOTIFICATION_STATUS_STOP
    private var errorPlayAttempts: Int = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayerInstance = (application as MyApplication).mediaPlayerInstance
        val player = mediaPlayerInstance.getPlayer()

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(SessionCallback())
            .build()

        // Add player listener
        player.addListener(playerListener)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaSession.player.removeListener(playerListener)
            mediaSession.release()
        } catch (e: Exception) {
            logDebug { "Exception onDestroy: ${e.message}" }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            logDebug { "PlayerListener: onPlaybackStateChanged = $playbackState" }
            updateNotificationBasedOnPlayerState()
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            logDebug { "PlayerListener: onMediaMetadataChanged = ${mediaMetadata.title}" }
            buildNotification(notificationStatus)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            logDebug { "PlayerListener: onPlayerError = $error" }
            handleError()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            logDebug { "PlayerListener: onIsPlayingChanged = $isPlaying" }
            if (isPlaying) {
                errorPlayAttempts = 0
            }
            updateNotificationBasedOnPlayerState()
        }
    }

    private fun updateNotificationBasedOnPlayerState() {
        val player = mediaSession.player
        val state = player.playbackState
        val isPlaying = player.isPlaying

        when {
            state == Player.STATE_BUFFERING -> {
                buildNotification(NOTIFICATION_STATUS_LOADING)
            }

            isPlaying -> {
                buildNotification(NOTIFICATION_STATUS_PLAY)
            }

            state == Player.STATE_IDLE || state == Player.STATE_ENDED -> {
                buildNotification(NOTIFICATION_STATUS_STOP)
            }

            else -> {
                buildNotification(NOTIFICATION_STATUS_STOP)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun handleError() {
        if (++errorPlayAttempts < MAX_ERROR_ATTEMPTS) {
            buildNotification(NOTIFICATION_STATUS_CONNECTING)
            handler.postDelayed({ mediaPlayerInstance.callPlay() }, 10000)
        } else {
            buildNotification(NOTIFICATION_STATUS_ERROR)
        }
    }

    private fun buildNotification(keyCode: Int?) {
        val player = mediaSession.player
        val metadata = player.mediaMetadata
        val title = metadata.title ?: ""
        val subtitle = metadata.artist ?: ""
        notificationStatus = keyCode ?: NOTIFICATION_STATUS_STOP

        val activityIntent = Intent(applicationContext, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, "default")
            .setContentTitle(title)
            .setContentText(subtitle)
            .setContentIntent(activityPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.BLACK)

        // If you have the androidx.media dependency, you can use MediaStyle:
        // implementation "androidx.media:media:1.x.x"
//        builder.setStyle(
//            androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mediaSession.sessionCompatToken)
//                .setShowActionsInCompactView(0)
//                .setShowCancelButton(true)
//                .setCancelButtonIntent(buildStopPendingIntent())
//        )

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            try {
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_screen)
                builder.setLargeIcon(bitmap)
            } catch (e: Exception) {
                logDebug { "exception while decoding logo img: ${e.message}" }
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initChannels(notificationManager)

        val notification = when (notificationStatus) {
            NOTIFICATION_STATUS_PLAY -> {
                builder.addAction(
                    R.drawable.ic_stop,
                    getString(R.string.stop),
                    buildStopPendingIntent()
                )
                builder.build().also { startForeground(NOTIFICATION_ID, it) }
            }
            NOTIFICATION_STATUS_CONNECTING -> {
                builder.setContentTitle(getString(R.string.connecting))
                builder.setContentText("")
                builder.addAction(
                    R.drawable.ic_stop,
                    getString(R.string.stop),
                    buildStopPendingIntent()
                )
                builder.build().also { startForeground(NOTIFICATION_ID, it) }
            }

            NOTIFICATION_STATUS_STOP -> {
                builder.addAction(
                    R.drawable.ic_play,
                    getString(R.string.play),
                    buildPlayPendingIntent()
                )
                val notif = builder.build()
                notificationManager.notify(NOTIFICATION_ID, notif)
                stopForeground(false)
                notif
            }

            NOTIFICATION_STATUS_LOADING -> {
                builder.setContentTitle(getString(R.string.loading))
                builder.setContentText("")
                builder.addAction(
                    R.drawable.ic_stop,
                    getString(R.string.stop),
                    buildStopPendingIntent()
                )
                builder.build().also { startForeground(NOTIFICATION_ID, it) }
            }

            NOTIFICATION_STATUS_ERROR -> {
                builder.addAction(
                    R.drawable.ic_play,
                    getString(R.string.play),
                    buildPlayPendingIntent()
                )
                val notif = builder.build()
                notificationManager.notify(NOTIFICATION_ID, notif)
                stopForeground(false)
                notif
            }

            else -> {
                builder.addAction(
                    R.drawable.ic_play,
                    getString(R.string.play),
                    buildPlayPendingIntent()
                )
                val notif = builder.build()
                notificationManager.notify(NOTIFICATION_ID, notif)
                stopForeground(false)
                notif
            }
        }
    }

    private fun buildPlayPendingIntent(): PendingIntent {
        // We can define a custom action intent, or rely on the UI sending player commands.
        // For simplicity, let's just start MainActivity. The user can press play in the UI.
        // If you need a direct command, consider a SessionCommand and onCustomCommand().
        val playIntent = Intent(applicationContext, MainActivity::class.java)
        return PendingIntent.getActivity(
            applicationContext,
            0,
            playIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildStopPendingIntent(): PendingIntent {
        // Similarly, just start MainActivity or handle via custom command if needed.
        // Or handle via media button event if user stops from the notification.
        val stopIntent = Intent(applicationContext, MainService::class.java)
        stopIntent.action = "ACTION_STOP"
        return PendingIntent.getService(
            applicationContext,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun initChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel =
            NotificationChannel("default", "DUBSTEP.FM", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    @UnstableApi
    inner class SessionCallback : MediaSession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            // Accept all controllers.
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            logDebug { "SessionCallback: onPostConnect from ${controller.packageName}" }
        }

        override fun onDisconnected(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) {
            logDebug { "SessionCallback: onDisconnected ${controller.packageName}" }
        }

        override fun onMediaButtonEvent(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            intent: Intent
        ): Boolean {
            // Handle media button events like play/pause/stop from headset or Bluetooth device.
            val player = session.player
            val keyEvent = intent.getParcelableExtra<android.view.KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (keyEvent != null && keyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                when (keyEvent.keyCode) {
                    android.view.KeyEvent.KEYCODE_MEDIA_STOP -> {
                        player.stop()
                        buildNotification(NOTIFICATION_STATUS_STOP)
                        return true
                    }

                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        player.play()
                        buildNotification(NOTIFICATION_STATUS_PLAY)
                        return true
                    }

                    android.view.KeyEvent.KEYCODE_HEADSETHOOK -> {
                        if (player.isPlaying) {
                            player.stop()
                            buildNotification(NOTIFICATION_STATUS_STOP)
                        } else {
                            player.play()
                            buildNotification(NOTIFICATION_STATUS_PLAY)
                        }
                        return true
                    }
                }
            }
            return false
        }
    }
}
