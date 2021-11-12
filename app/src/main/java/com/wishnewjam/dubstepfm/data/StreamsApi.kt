package com.wishnewjam.dubstepfm.data

import kotlinx.coroutines.flow.Flow

interface StreamsApi {
    fun getCurrentStream(): Flow<RadioStreamEntity>

    fun getAllStreams(): List<RadioStreamEntity>

    suspend fun updateStream(bitrate: Int)
}