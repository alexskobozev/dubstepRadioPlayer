package com.wishnewjam.dubstepfm.dagger

import android.app.Application
import com.wishnewjam.dubstepfm.MediaPlayerInstance
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AndroidModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): android.content.Context {
        return application
    }

    @Provides
    @Singleton
    fun provideMediaPlayer(): MediaPlayerInstance {
        return MediaPlayerInstance(application)
    }

    @Provides
    @Singleton
    fun provideUrl(): String {
        return "something"
    }

}