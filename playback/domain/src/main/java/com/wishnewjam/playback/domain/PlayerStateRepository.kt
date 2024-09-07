package com.wishnewjam.playback.domain

import kotlinx.coroutines.flow.StateFlow


interface PlayerStateRepository {
    val currentState: StateFlow<PlayerState>
    fun setCurrentState(state: PlayerState)
}

enum class PlayerState {
    LOADING,
    PLAYING,
    STOPPED,
}