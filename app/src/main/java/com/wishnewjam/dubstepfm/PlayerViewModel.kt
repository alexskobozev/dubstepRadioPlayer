package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.wishnewjam.playback.presentation.RadioService
import timber.log.Timber

class PlayerViewModel(private val radioServiceConnection: RadioServiceConnection) : ViewModel() {
    private var player: MediaController? = null // TODO: put mediacontroller to another file

    // LiveData or StateFlow objects for the UI to observe
    private val _nowPlayingText = MutableLiveData<String>("Nothing to play")
    val nowPlayingText: LiveData<String> get() = _nowPlayingText

    private val _progressBarVisible = MutableLiveData<Boolean>(false)
    val progressBarVisible: LiveData<Boolean> get() = _progressBarVisible

    private val _loadingVisible = MutableLiveData<Boolean>(false)
    val loadingVisible: LiveData<Boolean> get() = _loadingVisible


    // Function to handle play button click
    fun play() {
        // Start or resume playback in the radio service
        // radioServiceConnection.play()
        Timber.d("Push play button")
        player?.play()
        // Update the UI state
        _progressBarVisible.value = true
        _nowPlayingText.value = "Playing..."
    }

    // Function to handle stop button click
    fun stop() {
        // Stop playback in the radio service
        // radioServiceConnection.stop()

        // Update the UI state
        _progressBarVisible.value = false
        _nowPlayingText.value = "Nothing to play"
    }

    // Update the UI based on the radio service state
    // fun updateUI(radioState: RadioService.State) {
    //     when (radioState) {
    //         is RadioService.State.Playing -> {
    //             _nowPlayingText.value = "Playing: ${radioState.trackName}"
    //             _progressBarVisible.value = false
    //             _loadingVisible.value = false
    //         }
    //         is RadioService.State.Stopped -> {
    //             _nowPlayingText.value = "Nothing to play"
    //             _progressBarVisible.value = false
    //             _loadingVisible.value = false
    //         }
    //         is RadioService.State.Buffering -> {
    //             _nowPlayingText.value = "Buffering..."
    //             _progressBarVisible.value = true
    //             _loadingVisible.value = false
    //         }
    //         is RadioService.State.Loading -> {
    //             _nowPlayingText.value = "Loading..."
    //             _progressBarVisible.value = false
    //             _loadingVisible.value = true
    //         }
    //     }
    // }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun onStart(context: Context) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, RadioService::class.java)
        )
        Timber.d("Building MediaController")
        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                Timber.d("MediaController instance initiated")
                player = controllerFuture.get()
                _nowPlayingText.value = player!!.mediaMetadata.toString()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onCleared() {
        super.onCleared()
        // radioServiceConnection.unbindService()
    }
}
