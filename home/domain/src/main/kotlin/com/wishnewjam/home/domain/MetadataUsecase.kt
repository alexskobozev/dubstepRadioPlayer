package com.wishnewjam.home.domain

import kotlinx.coroutines.flow.Flow

interface MetadataUsecase {
    val playingText: Flow<String>
}