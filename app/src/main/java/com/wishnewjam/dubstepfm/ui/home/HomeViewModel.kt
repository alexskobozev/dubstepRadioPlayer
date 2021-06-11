package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import com.wishnewjam.dubstepfm.UiState

interface HomeViewModel {
    val playButtonState: LiveData<UiState>
    val nowPlaying: LiveData<String>
    val showNowPlaying: LiveData<Boolean>
    fun toggleButton()

}