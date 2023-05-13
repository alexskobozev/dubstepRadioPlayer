package com.wishnewjam.playback.data.di

import com.wishnewjam.playback.data.DefaultRadioServiceController
import com.wishnewjam.playback.domain.RadioServiceController
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        RadioServiceControllerModule::class,
    ]
)
interface PlaybackApiModule {
    @Binds
    fun radioServiceController(real: DefaultRadioServiceController): RadioServiceController
}