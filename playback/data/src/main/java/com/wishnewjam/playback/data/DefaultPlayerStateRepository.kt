package com.wishnewjam.playback.data

import com.wishnewjam.playback.domain.PlayerState
import com.wishnewjam.playback.domain.PlayerStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DefaultPlayerStateRepository @Inject constructor() : PlayerStateRepository {

    private val _currentState =
        MutableStateFlow(PlayerState.STOPPED)

    override val currentState: Flow<PlayerState> = _currentState

    override fun setCurrentState(state: PlayerState) {
        _currentState.value = state
    }
}