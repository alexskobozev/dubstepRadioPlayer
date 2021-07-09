package com.wishnewjam.dubstepfm.playback

import android.net.Uri

interface DubstepMediaPlayer {
    fun callPlay(uri: Uri)
    fun callStop()
}