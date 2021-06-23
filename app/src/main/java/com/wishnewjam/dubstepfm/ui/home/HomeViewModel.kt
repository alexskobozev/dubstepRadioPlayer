package com.wishnewjam.dubstepfm.ui.home

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import com.wishnewjam.dubstepfm.data.RadioStream
import com.wishnewjam.dubstepfm.ui.state.PlaybackState

interface HomeViewModel {

    val defaultStream : RadioStream
    val allStreams: Array<RadioStream>
    val initialPlayButtonState: Int
    val nowPlaying: LiveData<String?>
    val statusText: LiveData<String?>
    fun toggleButton()

    fun playbackStateChanged(state: PlaybackStateCompat?)
    fun updateStream(radioStream: RadioStream) {

    }

    val playbackState: LiveData<PlaybackState>
    val statusIcon: LiveData<Int?>
    val playButtonRes: LiveData<Int>
    val currentRadioStream: LiveData<RadioStream>
}