package com.wishnewjam.dubstepfm.notification

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.PlayerNotificationManager

class ExoMediaDescriptionAdapter(
    controller: MediaController?,
    resourcesProvider: NotificationResourceProvider,
    private val logoProvider: LogoProvider,
) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    private var contentIntent: PendingIntent?
    private var contentTitle: String = ""
    private var contentSubtitle: String? = null

    init {
        val mediaMetadata = controller?.mediaMetadata
        contentIntent = controller?.sessionActivity
        contentTitle = mediaMetadata?.title?.toString() ?: resourcesProvider.connectStatusString
        contentSubtitle = mediaMetadata?.subtitle?.toString()
    }


    override fun getCurrentContentTitle(player: Player): CharSequence {
        return contentTitle
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return contentIntent
    }

    override fun getCurrentContentText(player: Player): CharSequence? {
        return contentSubtitle
    }

    override fun getCurrentSubText(player: Player): CharSequence? {
        TODO("Not yet implemented")
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: androidx.media3.session.PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        TODO("Not yet implemented")
    }

//    override fun getCurrentLargeIcon(
//        player: Player,
//        callback: PlayerNotificationManager.BitmapCallback,
//    ): Bitmap? {
//        return logoProvider.getLogoBitmap()
//    }

}