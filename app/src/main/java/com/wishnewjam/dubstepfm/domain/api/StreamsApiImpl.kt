package com.wishnewjam.dubstepfm.domain.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.data.StreamsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StreamsApiImpl(context: Context) : StreamsApi {

    private val defaultBitrate = 128

    private val allStreams =
        sequenceOf(24, 64, defaultBitrate, 256)
            .map { RadioStreamEntity(it, "http://stream.dubstep.fm/${it}mp3") }.toList()


    private val Context.dataStore by preferencesDataStore("stream_prefs")
    private val dataStore = context.dataStore
    private val streamKey = intPreferencesKey("current_stream")

    override fun getCurrentStream(): Flow<RadioStreamEntity> {
        return dataStore.data
            .map { preferences ->
                findStreamByBitrate(preferences[streamKey])
            }
    }

    override fun getAllStreams(): List<RadioStreamEntity> {
        return allStreams
    }

    override suspend fun updateStream(bitrate: Int) {
        dataStore.edit { preferences ->
            preferences[streamKey] = findStreamByBitrate(bitrate).bitrate
        }
    }

    private fun findStreamByBitrate(bitrate: Int?) =
        allStreams.find { it.bitrate == bitrate } ?: allStreams.find { it.bitrate == defaultBitrate }!!

}