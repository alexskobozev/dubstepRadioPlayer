package com.wishnewjam.playback.domain

import kotlinx.coroutines.flow.Flow


interface PlayerStateRepository {
    val currentState: Flow<PlayerState>
    fun setCurrentState(state: PlayerState)
}

enum class PlayerState {
    LOADING,
    PLAYING,
    PAUSED,
}