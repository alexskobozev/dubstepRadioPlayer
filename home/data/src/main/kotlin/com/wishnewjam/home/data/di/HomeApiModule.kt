package com.wishnewjam.home.data.di

import com.wishnewjam.home.data.DefaultPlayerViewModel
import com.wishnewjam.home.data.GenericViewModelFactory
import com.wishnewjam.home.data.HomeMetadataUsecase
import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.home.domain.PlayerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface HomeApiModule {
    @Binds
    fun metadataUsecase(real: HomeMetadataUsecase): MetadataUsecase

    @Binds
    fun viewModel(real: DefaultPlayerViewModel): PlayerViewModel

    @Binds
    fun viewModelFactory(factory: GenericViewModelFactory<PlayerViewModel>): PlayerViewModelFactory
}