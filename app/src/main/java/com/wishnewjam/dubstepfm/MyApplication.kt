package com.wishnewjam.dubstepfm

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.wishnewjam.dubstepfm.dagger.AndroidModule
import com.wishnewjam.dubstepfm.dagger.ApplicationComponent
import com.wishnewjam.dubstepfm.dagger.DaggerApplicationComponent
import javax.inject.Inject

class MyApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var graph: ApplicationComponent
    }

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        graph = DaggerApplicationComponent.builder().androidModule(AndroidModule(this)).build()
        graph.inject(this)
    }
}