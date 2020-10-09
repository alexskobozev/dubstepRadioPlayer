package com.wishnewjam.dubstepfm

import androidx.multidex.MultiDexApplication

class MyApplication : MultiDexApplication() {
    val mediaPlayerInstance: MediaPlayerInstance by lazy {
        MediaPlayerInstance(this)
    }
}