package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wishnewjam.dubstepfm.UiState

class HomeViewModelPreview : HomeViewModel {
    override val playButtonState: LiveData<UiState> = MutableLiveData(UiState.Stop)
    override val nowPlaying: LiveData<String> = MutableLiveData("Now playing something")
    override val showNowPlaying: LiveData<Boolean> = MutableLiveData(false)

    override fun toggleButton() {

    }
}