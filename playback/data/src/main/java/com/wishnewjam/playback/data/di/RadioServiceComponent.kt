package com.wishnewjam.playback.data.di

import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.data.RadioService
import com.wishnewjam.stream.domain.StreamApi
import dagger.Component

@Component(
    modules = [
        RadioServiceModule::class
    ],
    dependencies = [
        MetadataApi::class,
        StreamApi::class,
    ]
)
interface RadioServiceComponent {

    fun inject(target: RadioService)

    @Component.Factory
    interface Factory {
        fun create(
            metadataApi: MetadataApi,
            streamApi: StreamApi,
        ): RadioServiceComponent
    }
}
