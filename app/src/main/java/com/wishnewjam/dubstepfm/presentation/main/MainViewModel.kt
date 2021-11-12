package com.wishnewjam.dubstepfm.presentation.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishnewjam.dubstepfm.common.Resource
import com.wishnewjam.dubstepfm.domain.usecase.main.GetStreamsInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MainViewModel(
    private val getStreamsInfo: GetStreamsInfo,
) : ViewModel() {

    private val _radioStreamsInfo: MutableState<MainState> = mutableStateOf(MainState(true, null, null, null))
    val radioStreamsInfo: State<MainState> = _radioStreamsInfo

    init {
        getStreamsInfo().onEach { result ->
            when (result) {
                is Resource.Loading -> _radioStreamsInfo.value = MainState(true, null, null, null)
                is Resource.Success -> _radioStreamsInfo.value = MainState(
                    false,
                    result.data?.bitratesList,
                    result.data?.activeListElementNum,
                    result.data?.currentStreamUri
                )
                else -> MainState(false, null, null, null)
            }
        }.launchIn(viewModelScope)
    }
}