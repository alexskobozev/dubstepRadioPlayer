package com.wishnewjam.dubstepfm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaStyleNotificationHelper

class RadioNotificationManager(
    private val context: Context,
    private val radioService: RadioService,
    private val notificationManager: NotificationManager
) {

    // companion object {
    //     const val NOTIFICATION_ID = 1
    // }
    //
    // private val packageName = context.packageName
    // private val pauseAction = createPauseAction()
    // private val playAction = createPlayAction()
    // private val stopAction = createStopAction()
    //
    // private fun createNotification(isPlaying: Boolean): Notification {
    //     val channelId = createNotificationChannel()
    //     val notificationBuilder = NotificationCompat.Builder(context, channelId)
    //
    //     // Customize the notification appearance
    //     notificationBuilder
    //         .setSmallIcon(R.drawable.ic_notification)
    //         .setContentTitle(context.getString(R.string.app_name))
    //         .setContentText(context.getString(R.string.now_playing))
    //         .setStyle(
    //             MediaStyleNotificationHelper.MediaStyle()
    //                 .setShowActionsInCompactView(0, 1)
    //         )
    //         .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    //         .setOnlyAlertOnce(true)
    //         .setAutoCancel(false)
    //         .setOngoing(true)
    //
    //     // Add actions
    //     if (isPlaying) {
    //         notificationBuilder.addAction(pauseAction)
    //     } else {
    //         notificationBuilder.addAction(playAction)
    //     }
    //     notificationBuilder.addAction(stopAction)
    //
    //     return notificationBuilder.build()
    // }
    //
    // fun updateNotification(isPlaying: Boolean) {
    //     val notification = createNotification()
    //     if (isPlaying) {
    //         notification.actions[0] = pauseAction
    //     } else {
    //         notification.actions[0] = playAction
    //     }
    //     notificationManager.notify(NOTIFICATION_ID, notification)
    // }
    //
    // private fun createNotificationChannel(): String {
    //     val channelId = "radio_channel_id"
    //     val channelName = "Radio Playback"
    //     val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
    //     notificationManager.createNotificationChannel(channel)
    //     return channelId
    // }
    //
    // private fun createPauseAction(): NotificationCompat.Action {
    //     val pauseIntent = Intent(context, RadioService::class.java).apply {
    //         action = RadioService.ACTION_PAUSE
    //     }
    //     val pendingIntent = PendingIntent.getService(context, 0, pauseIntent, 0)
    //     return NotificationCompat.Action(R.drawable.ic_pause, context.getString(R.string.pause), pendingIntent)
    // }
    //
    // private fun createPlayAction(): NotificationCompat.Action {
    //     val playIntent = Intent(context, RadioService::class.java).apply {
    //         action = RadioService.ACTION_PLAY
    //     }
    //     val pendingIntent = PendingIntent.getService(context, 0, playIntent, 0)
    //     return NotificationCompat.Action(R.drawable.ic_play, context.getString(R.string.play), pendingIntent)
    // }
    //
    // private fun createStopAction(): NotificationCompat.Action {
    //     val stopIntent = Intent(context, RadioService::class.java).apply {
    //         action = RadioService.ACTION_STOP
    //     }
    //     val pendingIntent = PendingIntent.getService(context, 0, stopIntent, 0)
    //     return NotificationCompat.Action(R.drawable.ic_stop, context.getString(R.string.stop), pendingIntent)
    // }
    //
    // fun showNotification(isPlaying: Boolean) {
    //     val notification = createNotification(isPlaying)
    //     radioService.startForeground(NOTIFICATION_ID, notification)
    // }
    //
    // fun hideNotification() {
    //     radioService.stopForeground(true)
    //     notificationManager.cancel(NOTIFICATION_ID)
    // }
}
