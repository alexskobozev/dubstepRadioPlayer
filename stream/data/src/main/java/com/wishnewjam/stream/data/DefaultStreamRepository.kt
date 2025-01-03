package com.wishnewjam.stream.data

import com.wishnewjam.stream.domain.StreamRepository
import com.wishnewjam.stream.domain.model.RadioStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DefaultStreamRepository @Inject constructor() : StreamRepository {

    private var savedStream: RadioStream = RadioStream.RadioStream128()

    override val currentRadioStream: Flow<RadioStream> =
        flowOf(savedStream)

    override val streams: Flow<List<RadioStream>>
        get() = flowOf(
            listOf(
                RadioStream.RadioStream24(),
                RadioStream.RadioStream64(),
                RadioStream.RadioStream128(),
                RadioStream.RadioStream256(),
            )
        )

    override fun setCurrentStream(radioStream: RadioStream) {
        savedStream = radioStream
    }
}
