package com.wishnewjam.dubstepfm

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RadioService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    // private lateinit var radioNotificationManager: RadioNotificationManager
    // private lateinit var streamRepository: StreamRepository

    // Define an internal state class and a flow to observe the state
    sealed class State {
        object Stopped : State()
        object Buffering : State()
        object Loading : State()
        data class Playing(val trackName: String) : State()
    }

    private val _state = MutableStateFlow<State>(State.Stopped)
    val state: StateFlow<State> get() = _state

    override fun onCreate() {
        super.onCreate()
        //
        // // Initialize the MediaPlayer, NotificationManager, and StreamRepository
        // mediaPlayer = MediaPlayer(/* Your media player configuration */)
        // radioNotificationManager = RadioNotificationManager(this)
        // streamRepository = StreamRepository()
        //
        // // Add listeners to the MediaPlayer for state changes, and update the notification
        // mediaPlayer.addListener(/* Implement listeners to update state, notification, and UI */)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle the intent, for example, to start, stop, or pause playback
        return START_NOT_STICKY
    }

    fun play() {
        // Start playback, and update the state and notification
        // val streamUrl = streamRepository.getStreamUrl()
        // mediaPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
        // mediaPlayer.prepare()
        // mediaPlayer.play()
        //
        // _state.value = State.Loading
        // radioNotificationManager.showNotification(/* Provide the necessary information */)
    }

    fun stop() {
        // Stop playback, release resources, and update the state and notification
        // mediaPlayer.stop()
        // mediaPlayer.release()
        //
        // _state.value = State.Stopped
        // radioNotificationManager.hideNotification()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Return the binder for communication with the service
        return RadioBinder()
    }

    inner class RadioBinder : Binder() {
        // Return a reference to the RadioService
        fun getService(): RadioService = this@RadioService
    }

    override fun onDestroy() {
        super.onDestroy()

        // Clean up resources when the service is destroyed
        mediaPlayer.release()
    }
}
