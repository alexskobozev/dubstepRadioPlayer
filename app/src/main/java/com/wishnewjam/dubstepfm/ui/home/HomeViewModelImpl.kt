package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.data.repository.RadioStreamRepositoryImpl
import com.wishnewjam.dubstepfm.ui.ResourcesProvider
import com.wishnewjam.dubstepfm.ui.state.PlaybackState
import com.wishnewjam.dubstepfm.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val streamRepositoryImpl: RadioStreamRepositoryImpl,
) :
    ViewModel(),
    HomeViewModel {

    private val _playButtonState = MutableLiveData<UiState>().apply {
        value = UiState.Play
    }

    private val _nowPlaying: MutableLiveData<String?> = MutableLiveData(null)
    private val _statusText: MutableLiveData<String?> = MutableLiveData(null)
    private val _userIntentPlayState: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val _playbackState = MutableLiveData<PlaybackState>().apply {
        value = PlaybackState.Stop
    }

    override val allStreamEntities: Array<RadioStreamEntity> = arrayOf(RadioStreamEntity.RadioStream24(),
        RadioStreamEntity.RadioStream64(),
        RadioStreamEntity.RadioStream128(),
        RadioStreamEntity.RadioStream256())

    override val initialPlayButtonState: Int = resourcesProvider.playButtonPlayIcon

    val userIntentPlayState: LiveData<Boolean?> = _userIntentPlayState
    override val statusText: LiveData<String?> = _statusText
    override val nowPlaying: LiveData<String?> = _nowPlaying
    override val playbackState: LiveData<PlaybackState> = _playbackState
    override val currentRadioStreamEntity: Flow<RadioStreamEntity> = streamRepositoryImpl.radioStream

    override val playButtonRes: LiveData<Int> = Transformations.switchMap(_playButtonState) {
        val res = when (it) {
            is UiState.Play, UiState.Error -> resourcesProvider.playButtonPlayIcon
            else -> resourcesProvider.playButtonStopIcon
        }
        MutableLiveData(res)
    }

    override val statusIcon: LiveData<Int?> = Transformations.switchMap(playbackState) {
        val res = when (it) {
            is PlaybackState.Play -> resourcesProvider.statusPlayIcon
            is PlaybackState.Stop -> resourcesProvider.statusPlayIcon // TODO: 18/06/2021 change for pause or stop
            is PlaybackState.Error -> resourcesProvider.statusErrorIcon
            else -> null
        }
        MutableLiveData(res)
    }

    override fun toggleButton() {
        when (_playButtonState.value) {
            is UiState.Play -> {
                _playbackState.value = PlaybackState.Loading
                _playButtonState.value = UiState.Stop
                _userIntentPlayState.value = true
            }
            is UiState.Stop -> {
                _playButtonState.value = UiState.Play
                _userIntentPlayState.value = false
            }
            is UiState.Loading -> {
            }
            is UiState.Error -> {
                _playButtonState.value = UiState.Play
                _userIntentPlayState.value = true
            }
        }
    }

    override fun updateStream(radioStreamEntity: RadioStreamEntity) = streamRepositoryImpl.updateStream(radioStreamEntity)

    fun nowPlayingTextChanged(track: String) {
        _nowPlaying.value = track
    }
}