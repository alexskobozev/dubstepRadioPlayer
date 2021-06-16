package com.wishnewjam.dubstepfm.playback

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState.PLAYBACK_POSITION_UNKNOWN
import android.media.session.PlaybackState.STATE_PLAYING
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
import com.wishnewjam.dubstepfm.ui.state.PlayerState

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
            setSessionActivity(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
            isActive = true
            token = sessionToken
            notificationBuilder.mediaController = controller
            notificationBuilder.token = sessionToken
        }

        val mediaPlayer = MediaPlayerInstance(context = context) { state -> onPlayerStateChanged(state) }
        mediaPlayerInstance = mediaPlayer
    }

    fun callSessionError(error: String) {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_ERROR,
                        PLAYBACK_POSITION_UNKNOWN,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY)
                .setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR,
                        error)
                .build())
    }

    fun callSessionPlay() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,
                        PLAYBACK_POSITION_UNKNOWN,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_PAUSE)
                .build())
    }

    private fun callSessionPause() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED,
                        PLAYBACK_POSITION_UNKNOWN,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY)
                .build())
    }

    fun callSessionLoading() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_BUFFERING,
                        PLAYBACK_POSITION_UNKNOWN,
                        0.0f)
                .setActions(PlaybackStateCompat.ACTION_PAUSE)
                .build())
    }

    fun callSessionStop() {
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_STOPPED,
                        PLAYBACK_POSITION_UNKNOWN,
                        0.0f)
                .build())
    }

    private fun dispatchPlay() {
        val notification = notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Loading)
        startForeground(notification)
        mediaPlayerInstance?.callPlay()
        startForeground(notification)
    }

    private fun dispatchStop() {
        mediaPlayerInstance?.callStop()
        notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Stop)
        stopForeground()
    }

    private fun dispatchPause() {
        mediaPlayerInstance?.callStop()
        val notification = notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Pause)
        startForeground(notification)
    }

    fun destroy() {
        try {
            mediaPlayerInstance?.destroy()
            mediaSession?.release()
        } catch (e: Exception) {
            Tools.logDebug { "Exception onDestroy: ${e.message}" }
        }
        mediaPlayerInstance = null
        mediaSession = null
    }

    fun handleIntent(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession,
                intent)
    }

    private fun onMediaPlayerError(error: String) {
        callSessionError(error)
        val not = notificationBuilder.buildNotification(NotificationBuilder.NotificationStatus.Error)
        startForeground(not)
    }

    private fun onPlayerStateChanged(status: PlayerState) {
        Tools.logDebug { "mediaPlayerCallback: onPlayerStateChanged, status = $status" }
        when (status) {
            is PlayerState.Play -> {
                val trackName = status.trackName
                if (trackName != null) {
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
                callSessionPlay()
            }
            is PlayerState.Buffering -> {
                callSessionLoading()
            }
            is PlayerState.Error -> {
                onMediaPlayerError(status.errorText)
            }
            else -> {
            }
        }
    }

    private fun startForeground(notification: Notification?) {
        startForeground.invoke(notification ?: return)
    }

    private fun stopForeground() {
        stopForeground.invoke()
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
            callSessionLoading()
            dispatchPlay()
        }

        override fun onPause() {
            super.onPause()
            Tools.logDebug { "mediaSessionCallback: onPause" }
            dispatchPause()

        }

        override fun onStop() {
            super.onStop()
            Tools.logDebug { "mediaSessionCallback: onStop" }
            dispatchStop()
        }
    }


    private fun handleMediaButtonIntent(event: KeyEvent?): Boolean {
        if (event != null) {
            val action = event.action
            if (action == KeyEvent.ACTION_DOWN) {
                if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                    callSessionPause()
                    dispatchPause()
                } else if (event.keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                    callSessionStop()
                    dispatchStop()
                } else if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    callSessionLoading()
                    dispatchPlay()
                } else if (event.keyCode == KeyEvent.KEYCODE_HEADSETHOOK || event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                    if (mediaSession?.controller?.playbackState?.playbackState == STATE_PLAYING) {
                        callSessionStop()
                        dispatchStop()
                    } else {
                        callSessionLoading()
                        dispatchPlay()
                    }
                }
                return true
            }
        }
        return false
    }
}