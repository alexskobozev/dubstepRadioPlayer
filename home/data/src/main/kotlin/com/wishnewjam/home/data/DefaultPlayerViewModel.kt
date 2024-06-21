package com.wishnewjam.home.data

import android.content.Context
import androidx.media3.session.MediaController
import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.PlayerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class DefaultPlayerViewModel @Inject constructor(
    metadataUsecase: MetadataUsecase,
) : PlayerViewModel() {
    private var player: MediaController? = null // TODO: put mediacontroller to another file

    val _state = MutableStateFlow(UiState())

    override val state: StateFlow<UiState> = _state.asStateFlow()

//    private val metadataRepository: MetadataRepository

    // LiveData or StateFlow objects for the UI to observe
    private val _nowPlayingText = MutableStateFlow("Nothing to play")
    val nowPlayingText: StateFlow<String> get() = _nowPlayingText.asStateFlow()

    private val _progressBarVisible = MutableStateFlow(false)
    val progressBarVisible: StateFlow<Boolean> get() = _progressBarVisible.asStateFlow()

    private val _loadingVisible = MutableStateFlow(false)
    val loadingVisible: StateFlow<Boolean> get() = _loadingVisible.asStateFlow()

//    val nowPlaying: StateFlow<String> = metadataRepository.currentTrack


    // Function to handle play button click
    override fun play() {
        // Start or resume playback in the radio service
        // radioServiceConnection.play()
        Timber.d("Push play button")
        player?.play()
        // Update the UI state
        _progressBarVisible.value = true
        _nowPlayingText.value = "Playing..."
    }

    // Function to handle stop button click
    override fun stop() {
        // Stop playback in the radio service
        // radioServiceConnection.stop()

        // Update the UI state
        _progressBarVisible.value = false
        _nowPlayingText.value = "Nothing to play"
    }
}
