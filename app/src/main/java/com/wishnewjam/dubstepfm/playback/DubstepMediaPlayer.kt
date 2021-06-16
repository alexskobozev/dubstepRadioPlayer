package com.wishnewjam.dubstepfm.playback

import com.wishnewjam.dubstepfm.legacy.MediaPlayerInstance

interface DubstepMediaPlayer {
    val status: Int
    var serviceCallback: MediaPlayerInstance.CallbackInterface?

    fun callPlay()
    fun callStop()
}