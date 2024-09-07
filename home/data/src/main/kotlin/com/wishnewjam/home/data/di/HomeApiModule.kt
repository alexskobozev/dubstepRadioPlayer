package com.wishnewjam.home.data.di

import com.wishnewjam.home.data.DefaultPlaybackStateUsecase
import com.wishnewjam.home.data.DefaultPlayerViewModel
import com.wishnewjam.home.data.GenericViewModelFactory
import com.wishnewjam.home.data.HomeMetadataUsecase
import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.UiPlayerStateUsecase
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.home.domain.PlayerViewModelFactory
import com.wishnewjam.playback.domain.PlayerState
import dagger.Binds
import dagger.Module

@Module
interface HomeApiModule {
    @Binds
    fun metadataUsecase(real: HomeMetadataUsecase): MetadataUsecase

    @Binds
    fun playbackStateUsecase(real: DefaultPlaybackStateUsecase): UiPlayerStateUsecase<PlayerState>

    @Binds
    fun viewModel(real: DefaultPlayerViewModel): PlayerViewModel

    @Binds
    fun viewModelFactory(factory: GenericViewModelFactory<PlayerViewModel>): PlayerViewModelFactory
}