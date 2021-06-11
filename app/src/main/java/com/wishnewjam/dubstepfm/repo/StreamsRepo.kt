package com.wishnewjam.dubstepfm.repo

import com.wishnewjam.dubstepfm.data.StreamType
import com.wishnewjam.dubstepfm.data.StreamsRepository
import kotlinx.coroutines.flow.MutableStateFlow

class StreamsRepo : StreamsRepository {
    override fun getStream(): MutableStateFlow<String> {
        TODO("Not yet implemented")
    }

    override fun toggleStream(type: StreamType) {
        TODO("Not yet implemented")
    }
}