package com.wishnewjam.dubstepfm.domain

interface StreamRepository {
    /**
     * Get the stream URL for the specified bitrate.
     * @param bitrate The bitrate of the desired stream.
     * @return The stream URL for the specified bitrate or the default URL if the bitrate is not found.
     */
    fun getStreamUrl(bitrate: String): String

    /**
     * Get all available stream URLs.
     * @return A Map of bitrate keys to stream URLs.
     */
    fun getAllStreamUrls(): Map<String, String>

    /**
     * Fetches metadata for the currently playing track.
     * This method should be implemented to fetch track metadata from the radio server.
     * @return A string containing the metadata of the currently playing track.
     */
    suspend fun fetchCurrentTrackMetadata(): String
}
