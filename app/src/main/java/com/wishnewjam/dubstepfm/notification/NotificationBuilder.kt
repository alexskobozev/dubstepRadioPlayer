package com.wishnewjam.dubstepfm.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.wishnewjam.dubstepfm.playback.MainService

class NotificationBuilder(
    private val context: Context,
    private val resourcesProvider: NotificationResourceProvider
) {
    private var lastStatus: NotificationStatus? = null

    sealed interface NotificationStatus {
        object Play : NotificationStatus
        object Stop : NotificationStatus
        object Pause : NotificationStatus
        object Loading : NotificationStatus
        object Error : NotificationStatus
    }

    init {
        initChannels(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }


    fun buildNotification(
        mediaSession: MediaSessionCompat?,
        status: NotificationStatus
    ): Notification? {
        val token = mediaSession?.sessionToken ?: return null
        val controller = mediaSession.controller ?: return null

        lastStatus = status
        return createNotification(status, createBuilder(token, controller))
    }

    fun updateMetaData(mediaSession: MediaSessionCompat?) {
        val controller = mediaSession?.controller ?: return

        val notifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = createBuilder(mediaSession.sessionToken, controller)

        val description = controller.metadata.description

        builder.setContentTitle(description?.title)
        builder.setContentText(description?.subtitle)
        builder.setSubText(description?.description)

        val notification = createNotification(lastStatus ?: return, builder)
        notifyManager.notify(MainService.NOTIFICATION_ID, notification)
    }

    private fun createBuilder(
        token: MediaSessionCompat.Token,
        controller: MediaControllerCompat
    ): NotificationCompat.Builder {
        val mediaMetadata = controller.metadata
        val description = mediaMetadata?.description
        val activity = controller.sessionActivity

        return NotificationCompat.Builder(
            context,
            resourcesProvider.channelId
        )
            .apply {
                setContentTitle(description?.title)
                setContentText(description?.subtitle)
                setSubText(description?.description)
                setContentIntent(activity)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setSmallIcon(resourcesProvider.notificationSmallIconRes)
                setProgress(0, 0, false)
                color = Color.BLACK
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(token)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(getDeleteIntent())
                )
                setDeleteIntent(getDeleteIntent())
            }
    }

    private fun createNotification(
        status: NotificationStatus,
        notificationBuilder: NotificationCompat.Builder
    ): Notification {
        val notification: Notification
        notificationBuilder.setOngoing(status == NotificationStatus.Play)
        when (status) {
            NotificationStatus.Play -> {
                addPauseAction(notificationBuilder)
                notification = notificationBuilder.build()
            }
            NotificationStatus.Loading -> {
                notificationBuilder.setContentTitle(resourcesProvider.connectStatusString)
                notificationBuilder.setContentText("")
                addPauseAction(notificationBuilder)
                notification = notificationBuilder.build()
            }
            NotificationStatus.Pause -> {
                addPlayAction(notificationBuilder)
                notification = notificationBuilder.build()
            }
            NotificationStatus.Stop -> {
                addPlayAction(notificationBuilder)
                notification = notificationBuilder.build()
            }
            NotificationStatus.Error -> {
                addPlayAction(notificationBuilder)
                notification = notificationBuilder.build()
            }
        }
        if (status == NotificationStatus.Play) notification.flags = Notification.FLAG_ONGOING_EVENT
        return notification
    }

    private fun addPlayAction(mBuilder: NotificationCompat.Builder) {
        mBuilder.addAction(
            resourcesProvider.playIconRes,
            resourcesProvider.playStatusString,
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_PLAY
            )
        )
    }

    private fun addPauseAction(mBuilder: NotificationCompat.Builder) {
        mBuilder.addAction(
            resourcesProvider.pauseIconRes,
            resourcesProvider.pauseStatusString,
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_PAUSE
            )
        )
    }

    private fun getDeleteIntent(): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_STOP
        )
    }

    private fun initChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            resourcesProvider.channelId, resourcesProvider.appName,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}