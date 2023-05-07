package com.wishnewjam.stream.domain

import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    val currentStreamUrl: Flow<String>
}
