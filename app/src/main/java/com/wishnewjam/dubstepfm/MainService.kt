package com.wishnewjam.dubstepfm

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.view.KeyEvent
import javax.inject.Inject

class MainService : Service() {

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance


    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mMediaButtonReceiver: MediaButtonIntentReceiver? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        MyApplication.graph.inject(this)
        initNotification()
        initHeadsetReceiver()
        return Service.START_STICKY
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

    private fun initNotification() {

        val resultIntent = Intent(this, MainActivity::class.java)

        val resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(resultPendingIntent)

        startForeground(NOTIFICATION_ID, mBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaButtonReceiver != null) {
            unregisterReceiver(mMediaButtonReceiver)
        }
    }

    inner class MediaButtonIntentReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val intentAction = intent.action
            if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
                return
            }
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return
            val action = event.action
            if (action == KeyEvent.ACTION_DOWN) {
                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                    mediaPlayerInstance.callPlayOrPause()
                }

                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    mediaPlayerInstance.callPlay()
                }
            }
            abortBroadcast()

        }
    }

    companion object {
        private val NOTIFICATION_ID = 43432
        val SP_KEY_BITRATE = "link"
    }

}
