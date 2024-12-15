package com.wishnewjam.playback.data.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.wishnewjam.playback.data.DefaultPlayerStateRepository
import com.wishnewjam.playback.data.usecase.DefaultSaveMetadataUseCase
import com.wishnewjam.playback.data.usecase.SaveMetaDataUseCase
import com.wishnewjam.playback.domain.PlayerStateRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable
import javax.inject.Singleton

@Module
interface RadioServiceControllerModule {

    @OptIn(UnstableApi::class)
    @Binds
    @Reusable
    fun saveMetaDataUseCase(real: DefaultSaveMetadataUseCase): SaveMetaDataUseCase

    @Binds
    @Reusable
    fun playerStateRepository(real: DefaultPlayerStateRepository): PlayerStateRepository
}
