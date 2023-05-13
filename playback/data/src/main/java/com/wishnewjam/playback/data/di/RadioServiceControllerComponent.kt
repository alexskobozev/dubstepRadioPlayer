package com.wishnewjam.playback.data.di

import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.data.DefaultRadioServiceController
import com.wishnewjam.stream.domain.StreamApi
import dagger.Component

@Component(
    modules = [
        RadioServiceControllerModule::class,
    ],
    dependencies = [
        MetadataApi::class,
        StreamApi::class,
    ]
)
interface RadioServiceControllerComponent {

    fun inject(target: DefaultRadioServiceController)

    @Component.Factory
    interface Factory {
        fun create(
            metadataApi: MetadataApi,
            streamApi: StreamApi,
        ): RadioServiceControllerComponent
    }
}
