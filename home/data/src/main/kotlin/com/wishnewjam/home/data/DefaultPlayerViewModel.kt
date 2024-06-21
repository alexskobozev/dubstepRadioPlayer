package com.wishnewjam.home.data

import androidx.lifecycle.viewModelScope
import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.playback.domain.PlaybackCommandHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DefaultPlayerViewModel @Inject constructor(
    private val metadataUsecase: MetadataUsecase,
    private val playbackCommandHandler: PlaybackCommandHandler,
) : PlayerViewModel() {

    val _state = MutableStateFlow(UiState())

    override val state: StateFlow<UiState> = _state.asStateFlow()

    override fun play() {
        Timber.d("Push play button")
        playbackCommandHandler.play()
        viewModelScope.launch {
            metadataUsecase.playingText.collectLatest { playingText ->
                _state.value = _state.value.copy(nowPlaying = playingText)
            }
        }
    }

    override fun stop() {
        Timber.d("Push stop button")
        playbackCommandHandler.stop()
        _state.value = _state.value.copy(nowPlaying = "Stopped")
    }
}
