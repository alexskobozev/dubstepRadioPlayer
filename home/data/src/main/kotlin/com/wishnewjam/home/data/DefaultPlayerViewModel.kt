package com.wishnewjam.home.data

import androidx.lifecycle.viewModelScope
import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.home.domain.UiPlayerStateUsecase
import com.wishnewjam.playback.domain.PlaybackCommandHandler
import com.wishnewjam.playback.domain.PlayerState
import com.wishnewjam.stream.domain.StreamRepository
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
    private val playbackStateUsecase: UiPlayerStateUsecase<PlayerState>,
    private val streamRepository: StreamRepository,
) : PlayerViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    override val state: StateFlow<UiState> = _uiState.asStateFlow()

    override fun initialize() {
        viewModelScope.launch {
            streamRepository.streams.collectLatest { streams ->
                _uiState.value = _uiState.value.copy(
                    streams = streams,
                )
            }
        }
        viewModelScope.launch {
            streamRepository.currentRadioStream.collectLatest { stream ->
                _uiState.value = _uiState.value.copy(
                    currentStream = stream,
                )
            }
        }
    }

    override fun clickPlayButton() {
        Timber.d("Push play button")
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            playbackStateUsecase.currentState.collectLatest { state ->
                handleNewState(state)
            }
        }
        viewModelScope.launch {
            metadataUsecase.playingText.collectLatest { playingText ->
                _uiState.value = _uiState.value.copy(
                    nowPlaying = playingText.nowPlaying,
                    year = playingText.year,
                )
            }
        }
        if (_uiState.value.isPlaying) {
            Timber.d("Push stop button")
            _uiState.value =
                _uiState.value.copy(isLoading = true, isPlaying = false)
            playbackCommandHandler.pause()
        } else {
            _uiState.value =
                _uiState.value.copy(isLoading = true, isPlaying = false)
            playbackCommandHandler.play()
        }

    }

    private fun handleNewState(state: PlayerState) {
        Timber.d("Ui received new state: $state")
        when (state) {
            PlayerState.LOADING -> _uiState.value =
                _uiState.value.copy(isLoading = true, isPlaying = false)

            PlayerState.PAUSED -> _uiState.value =
                _uiState.value.copy(isLoading = false, isPlaying = false)

            PlayerState.PLAYING -> _uiState.value =
                _uiState.value.copy(isLoading = false, isPlaying = true)
        }
    }
}
