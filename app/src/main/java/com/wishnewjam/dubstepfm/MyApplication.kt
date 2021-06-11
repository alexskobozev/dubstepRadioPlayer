package com.wishnewjam.dubstepfm

import androidx.multidex.MultiDexApplication
import com.wishnewjam.dubstepfm.data.AppContainer
import com.wishnewjam.dubstepfm.data.AppContainerImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : MultiDexApplication() {
//    lateinit var container: AppContainer


//    val mediaPlayerInstance: MediaPlayerInstance by lazy {
//        MediaPlayerInstance(this)
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        container = AppContainerImpl(this)
//    }
}