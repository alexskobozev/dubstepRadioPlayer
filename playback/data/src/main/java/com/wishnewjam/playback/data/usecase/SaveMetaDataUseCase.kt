package com.wishnewjam.playback.data.usecase

import androidx.media3.common.Metadata

interface SaveMetaDataUseCase {
    fun saveMetaData(metadata: Metadata)
}
