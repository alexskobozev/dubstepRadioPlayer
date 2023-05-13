package com.wishnewjam.playback.data.di

import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.domain.PlaybackApi
import com.wishnewjam.stream.domain.StreamApi
import dagger.Component

@Component(
    modules = [
        PlaybackApiModule::class
    ],
    dependencies = [
        MetadataApi::class,
        StreamApi::class,
    ]
)
interface PlaybackApiComponent : PlaybackApi {
    @Component.Factory
    interface Factory {
        fun create(
            metadataApi: MetadataApi,
            streamApi: StreamApi,
        ): PlaybackApiComponent
    }
}