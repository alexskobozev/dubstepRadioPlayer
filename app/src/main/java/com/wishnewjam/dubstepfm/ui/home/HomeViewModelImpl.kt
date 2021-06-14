package com.wishnewjam.dubstepfm.ui.home

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wishnewjam.dubstepfm.MediaState
import com.wishnewjam.dubstepfm.UiState
import com.wishnewjam.dubstepfm.ui.ResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(val resourcesProvider: ResourcesProvider) : ViewModel(),
        HomeViewModel {

    private val _playButtonState = MutableLiveData<UiState>().apply {
        value = UiState.Stop
    }

    private val _nowPlaying: MutableLiveData<String?> = MutableLiveData(null)
    private val _statusText: MutableLiveData<String?> = MutableLiveData(null)
    private val _mediaState = MutableLiveData<MediaState>().apply {
        value = MediaState.Stop
    }

    override val playButtonState: LiveData<UiState> = _playButtonState
    override val statusText: LiveData<String?> = _statusText
    override val nowPlaying: LiveData<String?> = _nowPlaying
    override val mediaState: LiveData<MediaState> = _mediaState

    override val statusIcon: LiveData<Int?> = Transformations.switchMap(mediaState) {
        val res = when (it) {
            is MediaState.Play  -> resourcesProvider.statusPlayIcon
            is MediaState.Error -> resourcesProvider.statusErrorIcon
            else                -> null
        }
        MutableLiveData(res)
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
    }

    override fun playbackStateChanged(state: PlaybackStateCompat?) {
        when (state?.state) {
            PlaybackStateCompat.STATE_PLAYING    -> {
                _mediaState.value = MediaState.Play
                _statusText.value = resourcesProvider.nowPlaying
            }
            PlaybackStateCompat.STATE_BUFFERING  -> {
                _mediaState.value = MediaState.Loading
                _statusText.value = resourcesProvider.loading
            }
            PlaybackStateCompat.STATE_CONNECTING -> {
                _mediaState.value = MediaState.Loading
                _statusText.value = resourcesProvider.loading
            }
            PlaybackStateCompat.STATE_ERROR      -> {
                _mediaState.value = MediaState.Error
                _playButtonState.value = UiState.Error
                _statusText.value =
                        "${resourcesProvider.errorText}: ${state.errorMessage}" // TODO: 13/06/2021 remove msg or leave for debug
                _nowPlaying.value = null
            }
            else                                 -> {
                _mediaState.value = MediaState.Stop
                _nowPlaying.value = null
                _statusText.value = null
            }
        }
    }

    fun nowPlayingTextChanged(track: String) {
        _nowPlaying.value = track
    }
}