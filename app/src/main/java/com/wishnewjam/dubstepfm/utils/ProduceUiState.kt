package com.wishnewjam.dubstepfm.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.wishnewjam.dubstepfm.data.Result
import com.wishnewjam.dubstepfm.ui.state.UiState
import com.wishnewjam.dubstepfm.ui.state.copyWithResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class ProduceUiState {

    data class ProducerResult<T>(val result: State<T>, val onRefresh: () -> Unit,
                                 val onClearError: () -> Unit)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun <Producer, T> produceUiState(producer: Producer, key: Any?,
                                     block: suspend Producer.() -> Result<T>): ProducerResult<UiState<T>> {
        val refreshChannel = remember { Channel<Unit>(Channel.CONFLATED) }
        val errorClearChannel = remember { Channel<Unit>(Channel.CONFLATED) }

        val result = produceState(UiState<T>(loading = true), producer, key) {
            value = UiState(loading = true)
            refreshChannel.send(Unit)

            launch {
                for (clearEvent in errorClearChannel) {
                    value = value.copy(exception = null)
                }
            }

            for (refreshEvent in refreshChannel) {
                value = value.copy(loading = true)
                value = value.copyWithResult(producer.block())
            }
        }
        return ProducerResult(result = result, onRefresh = { refreshChannel.offer(Unit) },
                onClearError = { errorClearChannel.offer(Unit) })
    }

}