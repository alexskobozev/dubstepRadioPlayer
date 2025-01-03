package com.wishnewjam.home.domain

import androidx.lifecycle.ViewModel
import com.wishnewjam.stream.domain.model.RadioStream
import kotlinx.coroutines.flow.StateFlow

abstract class PlayerViewModel : ViewModel() {
    abstract val state: StateFlow<UiState>

    data class UiState(
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val year: String = "",
        val nowPlaying: String = "",
        val streams: List<RadioStream> = listOf(),
        val currentStream: RadioStream? = null
    )

    abstract fun initialize()
    abstract fun clickPlayButton()
}