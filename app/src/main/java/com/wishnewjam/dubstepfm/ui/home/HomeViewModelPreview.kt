package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.ui.state.PlaybackState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeViewModelPreview : HomeViewModel {
    override val initialPlayButtonState: Int = R.drawable.ic_play
    override val nowPlaying: LiveData<String?> = MutableLiveData("Now playing something")
    override val allStreamEntities: Array<RadioStreamEntity> = arrayOf(RadioStreamEntity.RadioStream24(),
        RadioStreamEntity.RadioStream64(),
        RadioStreamEntity.RadioStream128(),
        RadioStreamEntity.RadioStream256())

    override fun toggleButton() {

    }

    override val playbackState: LiveData<PlaybackState> = MutableLiveData(PlaybackState.Stop)
    override val statusIcon: LiveData<Int?> = MutableLiveData(R.drawable.ic_play)
    override val playButtonRes: LiveData<Int> = MutableLiveData(R.drawable.ic_play)
    override val currentRadioStreamEntity: Flow<RadioStreamEntity> = flow { RadioStreamEntity.default }
    override val statusText: LiveData<String?> = MutableLiveData("Status")
}