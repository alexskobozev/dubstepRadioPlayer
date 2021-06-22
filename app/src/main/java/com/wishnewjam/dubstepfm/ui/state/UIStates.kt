package com.wishnewjam.dubstepfm.ui.state

sealed class PlayerState {
    object Undefined : PlayerState()
    object Play : PlayerState()
    object Pause : PlayerState()
    class Error(val errorText: String) : PlayerState()
    object Buffering : PlayerState()

    var trackName : String? = null
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

