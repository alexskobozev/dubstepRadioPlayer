package com.wishnewjam.dubstepfm.ui.home

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.data.RadioStream
import com.wishnewjam.dubstepfm.ui.state.PlaybackState

class HomeViewModelPreview : HomeViewModel {
    override val defaultStream: RadioStream = RadioStream.RadioStream128()
    override val initialPlayButtonState: Int = R.drawable.ic_play
    override val nowPlaying: LiveData<String?> = MutableLiveData("Now playing something")
    override val allStreams: Array<RadioStream> = arrayOf(RadioStream.RadioStream24(),
        RadioStream.RadioStream64(),
        RadioStream.RadioStream128(),
        RadioStream.RadioStream256())

    override fun toggleButton() {

    }

    override fun playbackStateChanged(state: PlaybackStateCompat?) {
    }

    override val playbackState: LiveData<PlaybackState> = MutableLiveData(PlaybackState.Stop)
    override val statusIcon: LiveData<Int?> = MutableLiveData(R.drawable.ic_play)
    override val playButtonRes: LiveData<Int> = MutableLiveData(R.drawable.ic_play)
    override val currentRadioStream: LiveData<RadioStream> =
        MutableLiveData(RadioStream.RadioStream128())
    override val statusText: LiveData<String?> = MutableLiveData("Status")
}