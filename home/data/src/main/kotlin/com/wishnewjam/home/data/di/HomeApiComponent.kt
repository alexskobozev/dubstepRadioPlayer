package com.wishnewjam.home.data.di

import com.wishnewjam.home.domain.HomeApi
import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.domain.PlaybackApi
import dagger.Component

@Component(
    modules = [
        HomeApiModule::class
    ],
    dependencies = [
        MetadataApi::class,
        PlaybackApi::class,
    ]
)
interface HomeApiComponent : HomeApi {
    @Component.Factory
    interface Factory {
        fun create(
            metadataApi: MetadataApi,
            playbackApi: PlaybackApi,
        ): HomeApiComponent
    }
}