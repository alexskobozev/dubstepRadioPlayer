package com.wishnewjam.playback.data.usecase

import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.icy.IcyInfo
import com.wishnewjam.metadata.domain.CurrentPlayingMetadata
import com.wishnewjam.metadata.domain.MetadataRepository
import javax.inject.Inject

@UnstableApi
class DefaultSaveMetadataUseCase @Inject constructor(
    private val metadataRepository: MetadataRepository
) : SaveMetaDataUseCase {
    override fun saveMetaData(metadata: Metadata) {
        metadataRepository.updateCurrentTrackMetadata(
            CurrentPlayingMetadata(
                (metadata[0] as IcyInfo).title.orEmpty()
            )
        ) // TODO: mapper
    }
}
