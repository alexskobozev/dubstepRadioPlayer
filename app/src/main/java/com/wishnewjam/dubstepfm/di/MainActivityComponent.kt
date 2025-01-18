package com.wishnewjam.dubstepfm.di

import com.wishnewjam.dubstepfm.MainActivity
import com.wishnewjam.home.domain.HomeApi
import com.wishnewjam.metadata.domain.MetadataApi
import com.wishnewjam.playback.domain.PlaybackApi
import dagger.Component

@Component(
    dependencies = [
        HomeApi::class,
        MetadataApi::class,
        PlaybackApi::class,
    ]
)
interface MainActivityComponent {
    fun inject(target: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            homeApi: HomeApi,
            metadataApi: MetadataApi,
            playbackApi: PlaybackApi,
        ): MainActivityComponent
    }
}