package com.wishnewjam.home.domain

import com.wishnewjam.home.domain.model.NowPlayingText
import kotlinx.coroutines.flow.Flow

interface MetadataUsecase {
    val playingText: Flow<NowPlayingText>
}