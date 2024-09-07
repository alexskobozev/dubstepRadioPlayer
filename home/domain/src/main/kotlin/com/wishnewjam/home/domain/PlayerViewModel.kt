package com.wishnewjam.home.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class PlayerViewModel : ViewModel() {
    abstract val state: StateFlow<UiState>

    data class UiState(
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val nowPlaying: String = "Nothing to play",
    )

    abstract fun play()

    abstract fun stop()
}