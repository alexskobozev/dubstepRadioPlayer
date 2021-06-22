package com.wishnewjam.dubstepfm.di

import android.content.Context
import com.wishnewjam.dubstepfm.notification.LogoProvider
import com.wishnewjam.dubstepfm.ui.ResourcesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
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
        return LogoProvider(context.resources, MainScope(), Dispatchers.IO)
    }
}