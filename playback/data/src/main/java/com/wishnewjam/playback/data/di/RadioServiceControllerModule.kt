package com.wishnewjam.playback.data.di

import com.wishnewjam.playback.data.usecase.DefaultSaveMetadataUseCase
import com.wishnewjam.playback.data.usecase.SaveMetaDataUseCase
import dagger.Binds
import dagger.Module

@Module
interface RadioServiceControllerModule {
    @Binds
    fun saveMetaDataUseCase(real: DefaultSaveMetadataUseCase): SaveMetaDataUseCase
}
