package com.wishnewjam.dubstepfm.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RadioStreamRepo(
    private val dataSource: StreamsDataSource,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) {
    var stream: RadioStream = RadioStream.RadioStream128()
    val radioStream: Flow<RadioStream> = dataSource.streamFlow

    fun updateStream(radioStream: RadioStream) {
        scope.launch {
            withContext(dispatcher) {
                dataSource.saveToDataStore(radioStream)
            }
        }
    }
}