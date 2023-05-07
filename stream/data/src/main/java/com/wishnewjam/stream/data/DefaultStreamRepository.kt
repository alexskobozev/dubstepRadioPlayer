package com.wishnewjam.stream.data

import com.wishnewjam.stream.domain.StreamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DefaultStreamRepository @Inject constructor() : StreamRepository {

    // private val streamUrls = mapOf(
    //     "24" to "http://stream.dubstep.fm/24mp3",
    //     "64" to "http://stream.dubstep.fm/64mp3",
    //     "128" to "http://stream.dubstep.fm/128mp3",
    //     "256" to "http://stream.dubstep.fm/256mp3",
    // )
    //
    // /**
    //  * Get the stream URL for the specified bitrate.
    //  * @param bitrate The bitrate of the desired stream.
    //  * @return The stream URL for the specified bitrate or the default URL if the bitrate is not found.
    //  */
    // override fun getStreamUrl(bitrate: String): String {
    //     return streamUrls[bitrate] ?: streamUrls["128"]!!
    // }
    //
    // /**
    //  * Get all available stream URLs.
    //  * @return A Map of bitrate keys to stream URLs.
    //  */
    // override fun getAllStreamUrls(): Map<String, String> {
    //     return streamUrls
    // }
    //
    // /**
    //  * Fetches metadata for the currently playing track.
    //  * This method should be implemented to fetch track metadata from the radio server.
    //  * @return A string containing the metadata of the currently playing track.
    //  */
    // override suspend fun fetchCurrentTrackMetadata(): String {
    //     // Implement API call or other method to fetch metadata
    //     // For the sake of the example, we return a dummy track title
    //     return "Dummy Track - Artist Name"
    // }
    override val currentStreamUrl: Flow<String> =
        flowOf("http://stream.dubstep.fm/128mp3")
}
