package com.wishnewjam.dubstepfm.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.wishnewjam.dubstepfm.playback.MediaPlayerInstance

class NotificationBuilder(
    private val context: Context,
    private val resourcesProvider: NotificationResourceProvider,
    private val logoProvider: LogoProvider
) {

    companion object {
        private const val NOTIFICATION_ID = 43432
    }

    fun createNotification(
        mediaPlayer: MediaPlayerInstance,
        mediaSession: MediaSessionCompat,
        showNotificationListener: (Int, Notification) -> Unit,
        hideNotificationListener: () -> Unit
    ) {
        val mediaDescriptionAdapter =
            ExoMediaDescriptionAdapter(
                mediaSession.controller,
                resourcesProvider, logoProvider
            )
        val listener = object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                super.onNotificationCancelled(notificationId, dismissedByUser)
                hideNotificationListener
                    .invoke()
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                super.onNotificationPosted(notificationId, notification, ongoing)
                showNotificationListener.invoke(notificationId, notification)
            }
        }
        val notificationManager =
            PlayerNotificationManager.Builder(
                context,
                NOTIFICATION_ID,
                resourcesProvider.channelId,
                mediaDescriptionAdapter
            )
                .setChannelNameResourceId(resourcesProvider.channelNameResourceId)
                .setChannelDescriptionResourceId(resourcesProvider.channelDescriptionResourceId)
                .setNotificationListener(listener)
                .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
                .build()
        notificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notificationManager.setSmallIcon(resourcesProvider.notificationSmallIconRes)
        notificationManager.setMediaSessionToken(mediaSession.sessionToken)
        notificationManager.setColor(Color.BLACK)
        notificationManager.setUsePlayPauseActions(true)
        notificationManager.setPlayer(mediaPlayer.exoPlayer)
    }

}