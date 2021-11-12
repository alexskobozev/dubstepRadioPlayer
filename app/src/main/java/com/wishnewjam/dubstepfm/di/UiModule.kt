package com.wishnewjam.dubstepfm.di

import android.content.Context
import com.wishnewjam.dubstepfm.data.repository.RadioStreamRepositoryImpl
import com.wishnewjam.dubstepfm.data.StreamsApi
import com.wishnewjam.dubstepfm.notification.LogoProvider
import com.wishnewjam.dubstepfm.ui.ResourcesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UiModule {

    @Provides
    @Singleton
    fun provideResourcesProvider(@ApplicationContext context: Context): ResourcesProvider {
        return ResourcesProvider(resources = context.resources)
    }

    @Provides
    @Singleton
    fun provideLogoProvider(@ApplicationContext context: Context): LogoProvider {
        return LogoProvider(context.resources, MainScope())
    }

    @Provides
    @Singleton
    fun provideRadioStreamRepo(streamsApi: StreamsApi): RadioStreamRepositoryImpl {
        return RadioStreamRepositoryImpl(streamsApi, MainScope())
    }

    @Provides
    @Singleton
    fun provideStreamsDataSource(@ApplicationContext context: Context): StreamsApi {
        return StreamsApi(context)
    }
}