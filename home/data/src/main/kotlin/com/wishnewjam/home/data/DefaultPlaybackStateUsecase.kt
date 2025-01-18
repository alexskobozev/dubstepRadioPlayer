package com.wishnewjam.home.data

import com.wishnewjam.home.domain.UiPlayerStateUsecase
import com.wishnewjam.playback.domain.PlayerState
import com.wishnewjam.playback.domain.PlayerStateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPlaybackStateUsecase @Inject constructor(
    playerStateRepository: PlayerStateRepository
) : UiPlayerStateUsecase<PlayerState> {
    override val currentState: Flow<PlayerState> = playerStateRepository.currentState
}
