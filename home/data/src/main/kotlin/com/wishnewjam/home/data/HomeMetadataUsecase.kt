package com.wishnewjam.home.data

import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.metadata.domain.MetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeMetadataUsecase @Inject constructor(
    private val repository: MetadataRepository,
) : MetadataUsecase {
    override val playingText: Flow<String>
        get() = repository.currentTrack.map { it.title }
}