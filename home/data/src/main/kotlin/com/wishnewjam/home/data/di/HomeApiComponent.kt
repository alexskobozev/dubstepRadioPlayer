package com.wishnewjam.home.data.di

import com.wishnewjam.home.domain.HomeApi
import com.wishnewjam.metadata.domain.MetadataApi
import dagger.Component

@Component(
    modules = [
        HomeApiModule::class
    ],
    dependencies = [
        MetadataApi::class,
    ]
)
interface HomeApiComponent : HomeApi {
    @Component.Factory
    interface Factory {
        fun create(
            metadataApi: MetadataApi,
        ): HomeApiComponent
    }
}