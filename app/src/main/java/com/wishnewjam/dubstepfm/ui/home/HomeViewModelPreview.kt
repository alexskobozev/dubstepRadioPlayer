package com.wishnewjam.dubstepfm.ui.home

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wishnewjam.dubstepfm.MediaState
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.UiState

class HomeViewModelPreview : HomeViewModel {
    override val playButtonState: LiveData<UiState> = MutableLiveData(UiState.Stop)
    override val nowPlaying: LiveData<String?> = MutableLiveData("Now playing something")

    override fun toggleButton() {

    }

    override fun playbackStateChanged(state: PlaybackStateCompat?) {
    }

    override val mediaState: LiveData<MediaState> = MutableLiveData(MediaState.Stop)
    override val statusIcon: LiveData<Int?> = MutableLiveData(R.drawable.ic_play)
    override val statusText: LiveData<String?> = MutableLiveData("Status")
}