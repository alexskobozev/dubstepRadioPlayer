package com.wishnewjam.dubstepfm.ui.home

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import com.wishnewjam.dubstepfm.ui.state.PlaybackState

interface HomeViewModel {
    val initialPlayButtonState: Int
    val nowPlaying: LiveData<String?>
    val statusText: LiveData<String?>
    fun toggleButton()

    fun playbackStateChanged(state: PlaybackStateCompat?)
    val playbackState: LiveData<PlaybackState>
    val statusIcon: LiveData<Int?>
    val playButtonRes: LiveData<Int>
}