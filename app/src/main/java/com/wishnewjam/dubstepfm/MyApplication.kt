package com.wishnewjam.dubstepfm

import android.app.Application
import android.content.Context
import android.util.Log
import com.wishnewjam.dubstepfm.dagger.AndroidModule
import com.wishnewjam.dubstepfm.dagger.ApplicationComponent
import com.wishnewjam.dubstepfm.dagger.DaggerApplicationComponent
import javax.inject.Inject

class MyApplication : Application() {

    companion object {
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance

    override fun onCreate() {
        super.onCreate()
        graph = DaggerApplicationComponent.builder().androidModule(AndroidModule(this)).build()
        graph.inject(this)
    }
}