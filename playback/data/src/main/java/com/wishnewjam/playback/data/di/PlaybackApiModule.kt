package com.wishnewjam.playback.data.di

import com.wishnewjam.playback.data.DefaultRadioServiceController
import com.wishnewjam.playback.domain.RadioServiceController
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module(
    includes = [
        RadioServiceControllerModule::class,
    ]
)
interface PlaybackApiModule {
    @Binds
    @Reusable
    fun radioServiceController(real: DefaultRadioServiceController): RadioServiceController
}