package com.wishnewjam.stream.domain

import com.wishnewjam.stream.domain.model.RadioStream
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    val currentRadioStream: Flow<RadioStream>
    val streams: Flow<List<RadioStream>>

    fun setCurrentStream(radioStream: RadioStream)
}
