package com.wishnewjam.home.data

import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.metadata.domain.MetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeMetadataUsecase @Inject constructor(
    val repository: MetadataRepository,
) : MetadataUsecase {
    override val playingText: Flow<String>
        get() = TODO("Not yet implemented")
}