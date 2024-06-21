package com.wishnewjam.dubstepfm.di

import android.app.Application
import com.wishnewjam.di.Api
import com.wishnewjam.home.data.di.DaggerHomeApiComponent
import com.wishnewjam.home.domain.HomeApi
import com.wishnewjam.metadata.data.di.DaggerMetadataComponent
import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.data.di.DaggerPlaybackApiComponent
import com.wishnewjam.playback.domain.PlaybackApi
import com.wishnewjam.stream.data.di.DaggerStreamComponent
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

    @Apis
    @Provides
    @Reusable
    @IntoMap
    @ClassKey(PlaybackApi::class)
    fun playbackApi(playbackApi: PlaybackApi): Api = playbackApi

    @Apis
    @Provides
    @Reusable
    @IntoMap
    @ClassKey(HomeApi::class)
    fun homeApi(homeApi: HomeApi): Api = homeApi
}

@Module
class LibsModule(private val application: Application) {
    @Provides
    @Reusable
    fun metadata(): MetadataApi = DaggerMetadataComponent.create()

    @Provides
    @Reusable
    fun stream(): StreamApi = DaggerStreamComponent.create()

    @Provides
    @Reusable
    fun playback(
        metadataApi: MetadataApi,
        streamApi: StreamApi,
    ): PlaybackApi = DaggerPlaybackApiComponent.factory().create(
        metadataApi = metadataApi,
        streamApi = streamApi,
    )

    @Provides
    @Reusable
    fun home(
        metadataApi: MetadataApi,
    ): HomeApi = DaggerHomeApiComponent.factory().create(
        metadataApi = metadataApi,
    )
}
