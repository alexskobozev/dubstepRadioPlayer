package com.wishnewjam.dubstepfm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel(private val radioServiceConnection: RadioServiceConnection) : ViewModel() {
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
        radioServiceConnection.play()

        // Update the UI state
        _progressBarVisible.value = true
        _nowPlayingText.value = "Playing..."
    }

    // Function to handle stop button click
    fun stop() {
        // Stop playback in the radio service
        radioServiceConnection.stop()

        // Update the UI state
        _progressBarVisible.value = false
        _nowPlayingText.value = "Nothing to play"
    }

    // Update the UI based on the radio service state
    fun updateUI(radioState: RadioService.State) {
        when (radioState) {
            is RadioService.State.Playing -> {
                _nowPlayingText.value = "Playing: ${radioState.trackName}"
                _progressBarVisible.value = false
                _loadingVisible.value = false
            }
            is RadioService.State.Stopped -> {
                _nowPlayingText.value = "Nothing to play"
                _progressBarVisible.value = false
                _loadingVisible.value = false
            }
            is RadioService.State.Buffering -> {
                _nowPlayingText.value = "Buffering..."
                _progressBarVisible.value = true
                _loadingVisible.value = false
            }
            is RadioService.State.Loading -> {
                _nowPlayingText.value = "Loading..."
                _progressBarVisible.value = false
                _loadingVisible.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        radioServiceConnection.unbindService()
    }
}
