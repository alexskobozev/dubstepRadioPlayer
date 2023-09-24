package com.wishnewjam.metadata.data.di

import com.wishnewjam.metadata.domain.MetadataApi
import dagger.Component

@Component(
    modules = [
        MetadataModule::class,
    ],
)
interface MetadataComponent : MetadataApi
