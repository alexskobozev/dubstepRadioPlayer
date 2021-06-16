package com.wishnewjam.dubstepfm.notification

import android.content.res.Resources
import com.wishnewjam.dubstepfm.R

class NotificationResourceProvider(resources: Resources) {
    val appName: String = "DUBSTEP.FM"
    val channelId: String = "default"
    val playStatusString: String = resources.getString(R.string.play)

    val connectStatusString: String = resources.getString(R.string.connecting)
    val pauseStatusString: String = resources.getString(R.string.stop)

    val playIconRes: Int = R.drawable.ic_play
    val notificationSmallIconRes: Int = R.drawable.ic_notification
    val pauseIconRes: Int = R.drawable.ic_stop
}