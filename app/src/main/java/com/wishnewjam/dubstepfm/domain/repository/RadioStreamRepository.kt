package com.wishnewjam.dubstepfm.domain.repository

import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import kotlinx.coroutines.flow.Flow

interface RadioStreamRepository {
    fun getCurrentStream(): Flow<RadioStreamEntity>

    fun getAllStreams(): Flow<List<RadioStreamEntity>>

    suspend fun updateStream(newBitrate: Int)
}