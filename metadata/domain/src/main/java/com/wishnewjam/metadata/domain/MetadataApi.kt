package com.wishnewjam.metadata.domain

import com.wishnewjam.di.Api

interface MetadataApi : Api {
    val metadataRepository: MetadataRepository
}
