package com.wishnewjam.dubstepfm.ui.home

import androidx.lifecycle.LiveData
import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.ui.state.PlaybackState
import kotlinx.coroutines.flow.Flow

interface HomeViewModel {

    val allStreamEntities: Array<RadioStreamEntity>
    val initialPlayButtonState: Int
    val nowPlaying: LiveData<String?>
    val statusText: LiveData<String?>
    fun toggleButton()

//    fun playbackStateChanged(state: PlaybackStateCompat?)
    fun updateStream(radioStreamEntity: RadioStreamEntity) {

    }

    val playbackState: LiveData<PlaybackState>
    val statusIcon: LiveData<Int?>
    val playButtonRes: LiveData<Int>
    val currentRadioStreamEntity: Flow<RadioStreamEntity>
}