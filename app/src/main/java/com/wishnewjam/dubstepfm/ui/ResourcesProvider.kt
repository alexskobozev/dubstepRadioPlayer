package com.wishnewjam.dubstepfm.ui

import android.content.res.Resources
import com.wishnewjam.dubstepfm.R

class ResourcesProvider(val resources: Resources) {

    val statusErrorIcon: Int = R.drawable.ic_error
    val statusPlayIcon: Int = R.drawable.ic_play

    val errorText: String = resources.getString(R.string.error)
    val loading: String = resources.getString(R.string.loading)
    val nowPlaying: String = resources.getString(R.string.now_playing)
}