package com.wishnewjam.dubstepfm.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.PlayerNotificationManager

class NotificationBuilder(
    private val context: Context,
    private val resourcesProvider: NotificationResourceProvider,
    private val logoProvider: LogoProvider,
) {
    companion object {
        private const val NOTIFICATION_ID = 43432
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun createNotification(
        mediaPlayer: Player,
        mediaSession: MediaSession,
        showNotificationListener: (Int, Notification) -> Unit,
        hideNotificationListener: () -> Unit,
    ) {
        val mediaDescriptionAdapter =
            ExoMediaDescriptionAdapter(null, resourcesProvider, logoProvider) // TODO: 03.11.2021 controller
        val listener = object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                super.onNotificationCancelled(notificationId, dismissedByUser)
                hideNotificationListener
                    .invoke()
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean,
            ) {
                super.onNotificationPosted(notificationId, notification, ongoing)
                if (ongoing) {
                    showNotificationListener.invoke(notificationId, notification)
                } else {
                    hideNotificationListener.invoke()
                }
            }
        }
        val notificationManager =
            PlayerNotificationManager.Builder(
                context,
                NOTIFICATION_ID,
                resourcesProvider.channelId,
            )
                .setMediaDescriptionAdapter(mediaDescriptionAdapter)
                .setChannelNameResourceId(resourcesProvider.channelNameResourceId)
                .setChannelDescriptionResourceId(resourcesProvider.channelDescriptionResourceId)
                .setNotificationListener(listener)
                .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
                .build()
        notificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notificationManager.setSmallIcon(resourcesProvider.notificationSmallIconRes)
//        notificationManager.setMediaSessionToken(mediaSession.token) todo token
        notificationManager.setColor(Color.BLACK)
        notificationManager.setUsePlayPauseActions(true)
        notificationManager.setPlayer(mediaPlayer)
        notificationManager.invalidate()
    }

}