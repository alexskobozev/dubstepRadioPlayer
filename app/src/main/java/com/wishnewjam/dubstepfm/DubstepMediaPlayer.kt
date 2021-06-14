package com.wishnewjam.dubstepfm

interface DubstepMediaPlayer {
    val status: Int
    var serviceCallback: MediaPlayerInstance.CallbackInterface?

    fun callPlay()
    fun callStop()
}