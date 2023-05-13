package com.wishnewjam.playback.presentation.di

import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.domain.PlaybackApi
import com.wishnewjam.playback.presentation.RadioService
import dagger.Component

@Component(
    dependencies = [
        PlaybackApi::class,
    ],
    modules = [
        RadioServiceModule::class,
    ],
)
interface RadioServiceComponent {

    fun inject(target: RadioService)

    @Component.Factory
    interface Factory {
        fun create(
            playbackApi: PlaybackApi,
        ): RadioServiceComponent
    }
}