package com.wishnewjam.dubstepfm.di

import android.app.Application
import com.wishnewjam.di.Api
import com.wishnewjam.metadata.data.di.DaggerMetadataComponent
import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.stream.domain.StreamApi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(
    includes = [
        LibsModule::class
    ]
)
class ApiModule {

    @Apis
    @Provides
    @Reusable
    @IntoMap
    @ClassKey(MetadataApi::class)
    fun metadataApi(metadataApi: MetadataApi): Api = metadataApi

    @Apis
    @Provides
    @Reusable
    @IntoMap
    @ClassKey(StreamApi::class)
    fun streamApi(streamApi: StreamApi): Api = streamApi
}

@Module
class LibsModule(private val application: Application) {
    @Provides
    @Reusable
    fun metadata(): MetadataApi = DaggerMetadataComponent.create()

    @Provides
    @Reusable
    fun stream(): StreamApi = DaggerStreamComponent.create()
}
