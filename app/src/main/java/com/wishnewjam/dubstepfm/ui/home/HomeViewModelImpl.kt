package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wishnewjam.dubstepfm.DubstepMediaPlayer
import com.wishnewjam.dubstepfm.PlayerState
import com.wishnewjam.dubstepfm.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(player: DubstepMediaPlayer) : ViewModel(),
        HomeViewModel {

    private val _playButtonState = MutableLiveData<UiState>().apply {
        value = UiState.Stop
    }

    private val _nowPlaying = MutableLiveData("")
    private val _playerState = MutableLiveData<PlayerState>().apply {
        value = PlayerState.Stop
    }

    override val playButtonState: LiveData<UiState> = _playButtonState
    override val nowPlaying: LiveData<String> = _nowPlaying
    override val showNowPlaying: LiveData<Boolean> = Transformations.switchMap(playButtonState) {
        MutableLiveData(it == UiState.Play) // change to player state
    }

    override fun toggleButton() {
        when (playButtonState.value) {
            is UiState.Stop    -> _playButtonState.value = UiState.Play
            is UiState.Play    -> _playButtonState.value = UiState.Stop
            is UiState.Loading -> {
            }
            is UiState.Error   -> {
            }
        }

        _nowPlaying.value = "Test now playing text"
    }
}