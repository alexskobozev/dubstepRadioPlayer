package com.wishnewjam.stream.data.di

import com.wishnewjam.stream.data.DefaultStreamRepository
import com.wishnewjam.stream.domain.StreamRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface StreamModule {
    @Binds
    @Reusable
    fun metadataRepository(
        real: DefaultStreamRepository
    ): StreamRepository
}
