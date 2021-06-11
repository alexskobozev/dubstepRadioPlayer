package com.wishnewjam.dubstepfm

internal object UIStates {
    const val STATUS_UNDEFINED = 0
    const val STATUS_PLAY = 3
    const val STATUS_STOP = 1
    const val STATUS_ERROR = 7
    const val STATUS_LOADING = 6
    const val STATUS_WAITING = 8
}

sealed interface UiState {
    object Play : UiState
    object Stop : UiState
    object Error : UiState
    object Loading : UiState
}

