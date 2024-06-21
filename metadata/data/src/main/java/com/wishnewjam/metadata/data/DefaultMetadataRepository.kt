package com.wishnewjam.metadata.data

import com.wishnewjam.metadata.domain.CurrentPlayingMetadata
import com.wishnewjam.metadata.domain.MetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DefaultMetadataRepository @Inject constructor() : MetadataRepository {

    private val _currentTrack =
        MutableStateFlow(CurrentPlayingMetadata("Default metadata")) // TODO: default value
    override val currentTrack: Flow<CurrentPlayingMetadata> = _currentTrack

    override fun updateCurrentTrackMetadata(currentPlayingMetadata: CurrentPlayingMetadata) {
        _currentTrack.value = currentPlayingMetadata
    }
}
