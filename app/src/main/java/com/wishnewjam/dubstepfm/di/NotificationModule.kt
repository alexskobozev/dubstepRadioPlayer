package com.wishnewjam.dubstepfm.di

import android.content.Context
import com.wishnewjam.dubstepfm.notification.NotificationBuilder
import com.wishnewjam.dubstepfm.notification.NotificationResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationBuilder {
        return NotificationBuilder(context, NotificationResourceProvider(resources = context.resources))
    }
}