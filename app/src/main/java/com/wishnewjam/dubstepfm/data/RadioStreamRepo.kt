package com.wishnewjam.dubstepfm.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RadioStreamRepo(
    private val dataSource: StreamsDataSource,
    private val scope: CoroutineScope,
) {
    val radioStream: Flow<RadioStream> = dataSource.streamFlow

    fun updateStream(radioStream: RadioStream) {
        scope.launch {
            dataSource.saveToDataStore(radioStream)
        }
    }
}