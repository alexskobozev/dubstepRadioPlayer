package com.wishnewjam.dubstepfm.di

import android.content.Context
import com.wishnewjam.dubstepfm.DubstepMediaPlayer
import com.wishnewjam.dubstepfm.MediaPlayerInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MyModule {

    @Provides
    @Singleton
    fun provideMediaPlayer(context: Context): DubstepMediaPlayer {
        return MediaPlayerInstance(context = context)
    }

//    @Provides
//    @Singleton
//    fun provideContext(): Context {
//        return context
//    }

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(): OkHttpClient {
//        return OkHttpClient.Builder()
//                .addInterceptor(HttpRequestInterceptor())
//                .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//                .client(okHttpClient)
//                .baseUrl("https://pokeapi.co/api/v2/")
//                .addConverterFactory(MoshiConverterFactory.create())
//                .build()
//    }
//}
}