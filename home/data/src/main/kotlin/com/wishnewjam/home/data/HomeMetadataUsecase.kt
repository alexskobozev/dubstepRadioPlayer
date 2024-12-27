package com.wishnewjam.home.data

import com.wishnewjam.home.domain.MetadataUsecase
import com.wishnewjam.home.domain.NowPlayingMapper
import com.wishnewjam.home.domain.model.NowPlayingText
import com.wishnewjam.metadata.domain.MetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeMetadataUsecase @Inject constructor(
    private val repository: MetadataRepository,
    private val nowPlayingMapper: NowPlayingMapper,
) : MetadataUsecase {
    override val playingText: Flow<NowPlayingText>
        get() = repository.currentTrack.map { nowPlayingMapper.nowPlaying(it.title) }
}