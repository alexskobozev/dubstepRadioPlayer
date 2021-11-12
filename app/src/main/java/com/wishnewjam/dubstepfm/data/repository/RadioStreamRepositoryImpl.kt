package com.wishnewjam.dubstepfm.data.repository

import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.data.StreamsApi
import com.wishnewjam.dubstepfm.domain.repository.RadioStreamRepository
import kotlinx.coroutines.flow.Flow

class RadioStreamRepositoryImpl(
    private val streamsApi: StreamsApi,
) : RadioStreamRepository {

    override fun getCurrentStream(): Flow<RadioStreamEntity> {
        return streamsApi.getCurrentStream()
    }

    override fun getAllStreams(): List<RadioStreamEntity> {
        return streamsApi.getAllStreams()
    }

    override suspend fun updateStream(newBitrate: Int) {
        streamsApi.updateStream(newBitrate)
    }
}