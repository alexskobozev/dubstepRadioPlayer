package com.wishnewjam.dubstepfm.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StreamsDataSource(context: Context) {

    private val Context.dataStore by preferencesDataStore("stream_prefs")
    private val dataStore = context.dataStore
    private val streamKey = intPreferencesKey("current_stream")


    suspend fun saveToDataStore(radioStream: RadioStream) {
        dataStore.edit { preferences ->
            preferences[streamKey] = radioStream.bitrate
        }
    }

    val streamFlow: Flow<RadioStream> = dataStore.data
        .map { preferences ->
            when (preferences[streamKey]) {
                24 -> RadioStream.RadioStream24()
                64 -> RadioStream.RadioStream64()
                128 -> RadioStream.RadioStream128()
                256 -> RadioStream.RadioStream256()
                else -> RadioStream.unknown
            }
        }
}