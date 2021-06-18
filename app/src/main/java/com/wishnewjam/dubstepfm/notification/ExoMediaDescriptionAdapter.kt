package com.wishnewjam.dubstepfm.notification

import android.app.PendingIntent
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class ExoMediaDescriptionAdapter(
    controller: MediaControllerCompat?,
    val resourcesProvider: NotificationResourceProvider,
    val logoProvider: LogoProvider
) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    private var contentIntent: PendingIntent?
    private var contentTitle: String = ""
    private var contentSubtitle: String? = null

    init {
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description
        contentIntent = controller?.sessionActivity
        contentTitle = description?.title?.toString() ?: resourcesProvider.connectStatusString
        contentSubtitle = description?.subtitle?.toString()
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

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        return logoProvider.getLogoBitmap()
    }

}