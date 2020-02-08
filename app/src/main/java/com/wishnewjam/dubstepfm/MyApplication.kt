package com.wishnewjam.dubstepfm

import android.app.Application

class MyApplication : Application() {
    val mediaPlayerInstance: MediaPlayerInstance by lazy {
        MediaPlayerInstance(this)
    }
}