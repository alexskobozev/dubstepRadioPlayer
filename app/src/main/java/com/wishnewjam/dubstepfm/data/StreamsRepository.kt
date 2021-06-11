package com.wishnewjam.dubstepfm.data

import kotlinx.coroutines.flow.MutableStateFlow

interface StreamsRepository {
    fun getStream(): MutableStateFlow<String>
    fun toggleStream(type: StreamType)
}