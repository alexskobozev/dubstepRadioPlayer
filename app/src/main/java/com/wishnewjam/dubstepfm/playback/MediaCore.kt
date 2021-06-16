package com.wishnewjam.dubstepfm.playback

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.media.session.MediaButtonReceiver
import com.wishnewjam.dubstepfm.MainActivity
import com.wishnewjam.dubstepfm.legacy.MediaPlayerInstance
import com.wishnewjam.dubstepfm.legacy.Tools
import com.wishnewjam.dubstepfm.notification.LogoProvider
import com.wishnewjam.dubstepfm.notification.NotificationBuilder
import com.wishnewjam.dubstepfm.ui.state.UIStates

class MediaCore(val notificationBuilder: NotificationBuilder,
                val logoProvider: LogoProvider,
                val startForeground: (Notification) -> Unit,
                val stopForeground: () -> Unit) {

    var token: MediaSessionCompat.Token? = null
        private set

    private var mediaSession: MediaSessionCompat? = null
    private var mediaPlayerInstance: MediaPlayerInstance? = null

    fun init(context: Context) {
        mediaSession = MediaSessionCompat(context,
                "PlayerService")
        mediaSession?.run {
            setPlaybackState(PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING,
                            0,
                            0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                    .build())
            val stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
            setPlaybackState(stateBuilder.build())

            val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
            val mediaPendingIntent: PendingIntent = PendingIntent.getBroadcast(context,
                    0,
                    mediaButtonIntent,
                    0)
            setMediaButtonReceiver(mediaPendingIntent)
            setCallback(MediaSessionCallback())
            setSessionActivity(PendingIntent.getActivity(context,
                    0,
                    Intent(context,
                            MainActivity::class.java),
                    0))
            isActive = true
            token = sessionToken
            notificationBuilder.mediaController = controller
            notificationBuilder.token = sessionToken
        }

        val mediaPlayer = MediaPlayerInstance(context = context)
        mediaPlayer.serviceCallback = object : MediaPlayerInstance.CallbackInterface {
            override fun onMetaDataTrackChange(trackName: String) {
                onTrackChange(trackName)
            }

            override fun onChangeStatus(status: Int) {
                onTrackChange(status)
            }

            override fun onError(error: String) {
                onMediaPlayerError(error)
            }
        }
        mediaPlayerInstance = mediaPlayer
    }

    fun error(error: String) {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_ERROR,
                        0,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY)
                .setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR,
                        error)
                .build())
    }

    fun play() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,
                        0,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_STOP)
                .build())
    }

    fun loading() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_BUFFERING,
                        0,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_STOP)
                .build())
    }

    fun stop() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_STOPPED,
                        0,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY)
                .build())
    }

    fun destroy() {
        try {
            mediaSession?.release()
        } catch (e: Exception) {
            Tools.logDebug { "Exception onDestroy: ${e.message}" }
        }
        mediaSession = null
    }

    fun handleIntent(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession,
                intent)
    }

    private fun onMediaPlayerError(error: String) {
        error(error)
        notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Error)
        stopForeground()
    }

    private fun onTrackChange(status: Int) {
        Tools.logDebug { "mediaPlayerCallback: onChangeStatus, status = $status" }
        when (status) {
            UIStates.STATUS_PLAY -> {
                play()
                val notification = notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Play)
                startForeground(notification)

            }
            UIStates.STATUS_LOADING -> {
                loading()
                val notification = notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Loading)
                startForeground(notification)
            }
            UIStates.STATUS_STOP, UIStates.STATUS_ERROR -> {
                stop()
                notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Stop)
                stopForeground()
            }
        }
    }

    private fun startForeground(notification: Notification?) {
        startForeground.invoke(notification ?: return)
    }

    private fun stopForeground() {
        stopForeground.invoke()
    }

    private fun onTrackChange(trackName: String) {
        val builder = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                        "")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,
                        trackName)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                        10000)

        logoProvider.updateMetadataBuilderWithLogo(builder)
        val metadataCompat = builder.build()
        mediaSession?.setMetadata(metadataCompat)

        val notification = notificationBuilder.updateNotification()
        startForeground(notification)
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            Tools.logDebug { "mediaSessionCallback: onMediaButtonEvent $mediaButtonEvent extras ${mediaButtonEvent?.extras}" }
            val intentAction = mediaButtonEvent?.action
            if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
                return false
            }
            val event: KeyEvent? = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
            return handleMediaButtonIntent(event)
        }

        override fun onCommand(command: String?,
                               extras: Bundle?,
                               cb: ResultReceiver?) {
            Tools.logDebug { "mediaSessionCallback: onCommand $command" }
            super.onCommand(command, extras, cb)
        }

        override fun onStop() {
            super.onStop()
            Tools.logDebug { "mediaSessionCallback: onStop" }
            mediaPlayerInstance?.callStop()
            notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Stop)
            stopForeground()
        }

        override fun onPlay() {
            super.onPlay()
            Tools.logDebug { "mediaSessionCallback: onPlay" }

//            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Request audio focus for playback, this registers the afChangeListener

//            val audioFocusRequest =
//                    AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
//                            .run {
//                                setOnAudioFocusChangeListener(afChangeListener)
//                                setAudioAttributes(AudioAttributesCompat.Builder()
//                                        .run {
//                                            setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
//                                            build()
//                                        })
//                                build()
//                            }
//
//            val result = AudioManagerCompat.requestAudioFocus(am,
//                    audioFocusRequest)
//            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                // Start the service
//                startService(Intent(context,
//                        MediaBrowserService::class.java))
//                // Set the session active  (and update metadata and state)
//                mediaSession.isActive = true
//                // start the player (custom call)
//                player.start()
//                // Register BECOME_NOISY BroadcastReceiver
//                registerReceiver(myNoisyAudioStreamReceiver,
//                        intentFilter)
//                // Put the service in the foreground, post notification
//                service.startForeground(id,
//                        myPlayerNotification)
//            }

            //////

            mediaPlayerInstance?.callPlay()
            val notification = notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Play)
            startForeground(notification)
        }

        override fun onPause() {
            super.onPause()
            Tools.logDebug { "mediaSessionCallback: onPause" }
        }
    }

    private fun handleMediaButtonIntent(event: KeyEvent?): Boolean {
        if (event != null) {
            val action = event.action
            if (action == KeyEvent.ACTION_DOWN) {
                var status: NotificationBuilder.NotificationStatus? = null
                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                    status = NotificationBuilder.NotificationStatus.Stop
                    mediaPlayerInstance?.callStop()
                } else if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    status = NotificationBuilder.NotificationStatus.Play
                    mediaPlayerInstance?.callPlay()
                } else if (event.keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                    if (mediaPlayerInstance?.status == UIStates.STATUS_PLAY) {
                        status = NotificationBuilder.NotificationStatus.Stop
                        mediaPlayerInstance?.callStop()
                    } else {
                        status = NotificationBuilder.NotificationStatus.Play
                        mediaPlayerInstance?.callPlay()
                    }
                }
                if (status == null) {
                    return false
                }
                notificationBuilder.buildNotification(status)
                // TODO: 16/06/2021 start/stop service?
                return true
            }
        }
        return false
    }
}