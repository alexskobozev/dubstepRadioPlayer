package com.wishnewjam.dubstepfm.domain.usecase.main

import android.net.Uri
import com.wishnewjam.dubstepfm.common.Resource
import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.domain.model.RadioStreamsInfo
import com.wishnewjam.dubstepfm.domain.repository.RadioStreamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class GetStreamsInfo @Inject constructor(
    private val repository: RadioStreamRepository,
) {
    operator fun invoke(): Flow<Resource<RadioStreamsInfo>> =
        merge(flow {
            emit(Resource.Loading<RadioStreamsInfo>())
        },
            repository.getAllStreams()
                .combine(repository.getCurrentStream()) { allStreams: List<RadioStreamEntity>,
                                                          currentStream: RadioStreamEntity ->
                    Resource.Success<RadioStreamsInfo>(
                        RadioStreamsInfo(
                            allStreams.map { it.bitrate },
                            allStreams.indexOf(currentStream),
                            Uri.parse(currentStream.url)
                        )
                    )
                }
        )
}