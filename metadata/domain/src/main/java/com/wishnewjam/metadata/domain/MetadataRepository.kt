package com.wishnewjam.metadata.domain

import kotlinx.coroutines.flow.Flow

interface MetadataRepository {

    val currentTrack: Flow<CurrentPlayingMetadata>
    fun updateCurrentTrackMetadata(currentPlayingMetadata: CurrentPlayingMetadata)
}
