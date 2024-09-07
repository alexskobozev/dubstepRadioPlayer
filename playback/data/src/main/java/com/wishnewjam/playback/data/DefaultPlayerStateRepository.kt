package com.wishnewjam.playback.data

import com.wishnewjam.playback.domain.PlayerState
import com.wishnewjam.playback.domain.PlayerStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

class DefaultPlayerStateRepository @Inject constructor() : PlayerStateRepository {

    private val _currentState =
        MutableStateFlow(PlayerState.STOPPED)

    override val currentState: StateFlow<PlayerState> = _currentState

    override fun setCurrentState(state: PlayerState) {
        Timber.d("Player produced new state: $state")
        _currentState.value = state
    }
}