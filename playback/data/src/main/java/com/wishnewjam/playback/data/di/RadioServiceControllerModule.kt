package com.wishnewjam.playback.data.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.wishnewjam.playback.data.DefaultPlayerStateRepository
import com.wishnewjam.playback.data.usecase.DefaultSaveMetadataUseCase
import com.wishnewjam.playback.data.usecase.SaveMetaDataUseCase
import com.wishnewjam.playback.domain.PlayerStateRepository
import dagger.Binds
import dagger.Module

@Module
interface RadioServiceControllerModule {

    @OptIn(UnstableApi::class)
    @Binds
    fun saveMetaDataUseCase(real: DefaultSaveMetadataUseCase): SaveMetaDataUseCase

    @Binds
    fun playerStateRepository(real: DefaultPlayerStateRepository): PlayerStateRepository
}
