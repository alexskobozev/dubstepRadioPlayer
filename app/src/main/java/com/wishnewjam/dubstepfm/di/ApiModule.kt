package com.wishnewjam.dubstepfm.di

import android.app.Application
import com.wishnewjam.di.Api
import com.wishnewjam.metadata.data.di.DaggerMetadataComponent
import com.wishnewjam.metadata.domain.MetadataApi
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
}

@Module
class LibsModule(private val application: Application) {
    @Provides
    @Reusable
    fun metadata(): MetadataApi = DaggerMetadataComponent.create()
}
