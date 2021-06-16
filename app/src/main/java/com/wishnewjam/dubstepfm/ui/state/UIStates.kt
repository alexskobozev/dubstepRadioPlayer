package com.wishnewjam.dubstepfm.ui.state

sealed class PlayerState {
    object Undefined : PlayerState()
    class Play(val trackName: String?) : PlayerState()
    object Stop : PlayerState()
    class Error(val errorText: String) : PlayerState()
    object Buffering : PlayerState()
}

sealed interface UiState {
    object Play : UiState
    object Stop : UiState
    object Error : UiState
    object Loading : UiState
}


sealed interface PlaybackState {
    object Undefined : PlaybackState
    object Play : PlaybackState
    object Stop : PlaybackState
    object Error : PlaybackState
    object Loading : PlaybackState
}

