package com.wishnewjam.dubstepfm.ui.home

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import com.wishnewjam.dubstepfm.ui.state.MediaState
import com.wishnewjam.dubstepfm.ui.state.UiState

interface HomeViewModel {
    val playButtonState: LiveData<UiState>
    val nowPlaying: LiveData<String?>
    val statusText: LiveData<String?>
    fun toggleButton()

    fun playbackStateChanged(state: PlaybackStateCompat?)
    val mediaState: LiveData<MediaState>
    val statusIcon: LiveData<Int?>
}