package com.wishnewjam.home.data

import com.wishnewjam.home.domain.UiPlayerStateUsecase
import com.wishnewjam.playback.domain.PlayerState
import com.wishnewjam.playback.domain.PlayerStateRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DefaultPlaybackStateUsecase @Inject constructor(
    playerStateRepository: PlayerStateRepository
) : UiPlayerStateUsecase {
    override val currentState: StateFlow<PlayerState> = playerStateRepository.currentState
}
