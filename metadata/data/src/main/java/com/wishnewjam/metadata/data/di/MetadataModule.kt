package com.wishnewjam.metadata.data.di

import com.wishnewjam.metadata.data.DefaultMetadataRepository
import com.wishnewjam.metadata.domain.MetadataRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface MetadataModule {
    @Binds
    @Reusable
    fun metadataRepository(
        real: DefaultMetadataRepository
    ): MetadataRepository
}
