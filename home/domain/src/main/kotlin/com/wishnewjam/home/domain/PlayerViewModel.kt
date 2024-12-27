package com.wishnewjam.home.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class PlayerViewModel : ViewModel() {
    abstract val state: StateFlow<UiState>

    data class UiState(
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val year: String = "",
        val nowPlaying: String = "",
    )

    abstract fun clickPlayButton()
}