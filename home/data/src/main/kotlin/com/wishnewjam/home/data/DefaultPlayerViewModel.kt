package com.wishnewjam.home.data

import androidx.lifecycle.viewModelScope
import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.home.domain.UiPlayerStateUsecase
import com.wishnewjam.playback.domain.PlaybackCommandHandler
import com.wishnewjam.playback.domain.PlayerState
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
) : PlayerViewModel() {

    private val _state = MutableStateFlow(UiState())

//    private fun createFlow(): Flow<UiState> = combine(
//        playbackStateUsecase.currentState,
//        metadataUsecase.playingText,
//    ) { state, text ->
//        UiState(
//            isLoading = state != PlayerState.LOADING,
//            isPlaying = state == PlayerState.PLAYING,
//            nowPlaying = text,
//        )
//    }

    override val state: StateFlow<UiState> = _state.asStateFlow()

    override fun play() {
        Timber.d("Push play button")
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            playbackStateUsecase.currentState.collectLatest { state ->
                handleNewState(state)
            }
        }
        viewModelScope.launch {
            metadataUsecase.playingText.collectLatest { playingText ->
                _state.value = _state.value.copy(nowPlaying = playingText)
            }
        }
        playbackCommandHandler.play()
    }

    private fun handleNewState(state: PlayerState) {
        Timber.d("Ui received new state: $state")
        // TODO: other states
        when (state) {
            PlayerState.LOADING -> _state.value =
                _state.value.copy(isLoading = true, isPlaying = false)

            PlayerState.STOPPED -> _state.value =
                _state.value.copy(isLoading = false, isPlaying = false)

            PlayerState.PLAYING -> _state.value =
                _state.value.copy(isLoading = false, isPlaying = true)
        }
    }

    override fun stop() {
        Timber.d("Push stop button")
        playbackCommandHandler.stop()
        _state.value = _state.value.copy(nowPlaying = "Stopped")
    }
}
