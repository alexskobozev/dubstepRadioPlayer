package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wishnewjam.dubstepfm.UiState

class HomeViewModel : ViewModel() {

    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState.Stop
    }

    val playButtonState: LiveData<UiState> = _uiState

    fun toggleButton() {
        when (playButtonState.value) {
            is UiState.Stop    -> _uiState.value = UiState.Play
            is UiState.Play    -> _uiState.value = UiState.Stop
            is UiState.Loading -> {
            }
            is UiState.Error   -> {
            }
        }
    }
}