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

class NotificationBuilder(val context: Context,
                          val resourcesProvider: NotificationResourceProvider) {

    companion object {
        const val NOTIFICATION_ID = 43432
    }


    var token: MediaSessionCompat.Token? = null
    var mediaController: MediaControllerCompat? = null

    private var lastStatus: NotificationStatus? = null

    sealed interface NotificationStatus {
        object Play : NotificationStatus
        object Stop : NotificationStatus
        object Loading : NotificationStatus
        object Error : NotificationStatus
    }


    fun buildNotification(status: NotificationStatus): Notification? {
        val controller = mediaController ?: return null
        val mediaMetadata = controller.metadata
        val description = mediaMetadata?.description
        val activity = controller.sessionActivity
        lastStatus = status

        val notificationBuilder = NotificationCompat.Builder(context,
                "default")
                .apply {
                    setContentTitle(description?.title)
                    setContentText(description?.subtitle)
                    setSubText(description?.description)
                    setContentIntent(activity)
                    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    setSmallIcon(resourcesProvider.notificationSmallIconRes)
                    color = Color.BLACK
                    setDeleteIntent(getDeleteIntent())
                    setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0)
                            .setMediaSession(token)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(getDeleteIntent()))
                }

        val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initChannels(notifyManager)
        // TODO: 16/06/2021 put notify outside
        when (status) {
            NotificationStatus.Play -> {
                addStopAction(notificationBuilder)
                return notificationBuilder.build()
            }
            NotificationStatus.Loading -> {
                notificationBuilder.setContentTitle(resourcesProvider.connectStatusString)
                notificationBuilder.setContentText("")
                addStopAction(notificationBuilder)
                return notificationBuilder.build()
            }
            NotificationStatus.Stop -> {
                addPlayAction(notificationBuilder)
                notifyManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                return notificationBuilder.build()
            }
            NotificationStatus.Error -> {
                addPlayAction(notificationBuilder)
                notifyManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                return notificationBuilder.build()

            }
            else -> {
                addPlayAction(notificationBuilder)
                notifyManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                return notificationBuilder.build()
            }
        }
    }

    private fun addPlayAction(mBuilder: NotificationCompat.Builder) {
        mBuilder.addAction(resourcesProvider.playIconRes,
                resourcesProvider.playStatusString,
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PLAY))
    }

    private fun addStopAction(mBuilder: NotificationCompat.Builder) {
        mBuilder.addAction(resourcesProvider.stopIconRes,
                resourcesProvider.stopStatusString,
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))
    }

    private fun getDeleteIntent(): PendingIntent? {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                PlaybackStateCompat.ACTION_STOP)
    }

    private fun initChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(resourcesProvider.channelId, resourcesProvider.appName,
                NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    fun updateNotification(): Notification? {
        return buildNotification(lastStatus ?: return null)
    }

}