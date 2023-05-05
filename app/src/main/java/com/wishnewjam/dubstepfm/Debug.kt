package com.wishnewjam.dubstepfm

import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.util.EventLogger
import timber.log.Timber

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun gebugListener() = object : AnalyticsListener {
    override fun onMetadata(
        eventTime: AnalyticsListener.EventTime,
        metadata: Metadata
    ) {
        Timber.d("Metadata changed: $metadata")
        super.onMetadata(eventTime, metadata)
    }

    override fun onPlayerError(
        eventTime: AnalyticsListener.EventTime,
        error: PlaybackException
    ) {
        Timber.d(error)
        super.onPlayerError(eventTime, error)
    }

    override fun onEvents(
        player: Player,
        events: AnalyticsListener.Events
    ) {
        for (i in 0 until events.size()) {
            Timber.d("Exoplayer event: ${events.get(i).eventToString()}")
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun AnalyticsListener.Events.printDebug() {
    for (i in 0 until this.size()) {
        Timber.d("Exoplayer event: ${this.get(i).eventToString()}")
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun Int.eventToString(): String {
    return when (this) {
        Player.EVENT_TIMELINE_CHANGED -> "Timeline changed"
        Player.EVENT_MEDIA_ITEM_TRANSITION -> "Media item transition"
        Player.EVENT_TRACKS_CHANGED -> "Tracks changed"
        Player.EVENT_IS_LOADING_CHANGED -> "Is loading changed"
        Player.EVENT_PLAYBACK_STATE_CHANGED -> "Playback state changed"
        Player.EVENT_PLAY_WHEN_READY_CHANGED -> "Play when ready changed"
        Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED -> "Playback suppression reason changed"
        Player.EVENT_IS_PLAYING_CHANGED -> "Is playing changed"
        Player.EVENT_REPEAT_MODE_CHANGED -> "Repeat mode changed"
        Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED -> "Shuffle mode enabled changed"
        Player.EVENT_PLAYER_ERROR -> "Player error"
        Player.EVENT_POSITION_DISCONTINUITY -> "Position discontinuity"
        Player.EVENT_PLAYBACK_PARAMETERS_CHANGED -> "Playback parameters changed"
        Player.EVENT_AVAILABLE_COMMANDS_CHANGED -> "Available commands changed"
        Player.EVENT_MEDIA_METADATA_CHANGED -> "Media metadata changed"
        Player.EVENT_PLAYLIST_METADATA_CHANGED -> "Playlist metadata changed"
        Player.EVENT_SEEK_BACK_INCREMENT_CHANGED -> "Seek back increment changed"
        Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED -> "Seek forward increment changed"
        Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED -> "Max seek to previous position changed"
        Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED -> "Track selection parameters changed"
        Player.EVENT_DEVICE_INFO_CHANGED -> "Device info changed"
        Player.EVENT_DEVICE_VOLUME_CHANGED -> "Device volume changed"
        EventLogger.EVENT_LOAD_STARTED -> "Load started"
        EventLogger.EVENT_LOAD_COMPLETED -> "Load completed"
        EventLogger.EVENT_LOAD_CANCELED -> "Load canceled"
        EventLogger.EVENT_LOAD_ERROR -> "Load error"
        EventLogger.EVENT_DOWNSTREAM_FORMAT_CHANGED -> "Downstream format changed"
        EventLogger.EVENT_UPSTREAM_DISCARDED -> "Upstream discarded"
        EventLogger.EVENT_BANDWIDTH_ESTIMATE -> "Bandwidth estimate"
        EventLogger.EVENT_METADATA -> "Metadata"
        EventLogger.EVENT_CUES -> "Cues"
        EventLogger.EVENT_AUDIO_ENABLED -> "Audio enabled"
        EventLogger.EVENT_AUDIO_DECODER_INITIALIZED -> "Audio decoder initialized"
        EventLogger.EVENT_AUDIO_INPUT_FORMAT_CHANGED -> "Audio input format changed"
        EventLogger.EVENT_AUDIO_POSITION_ADVANCING -> "Audio position advancing"
        EventLogger.EVENT_AUDIO_UNDERRUN -> "Audio underrun"
        EventLogger.EVENT_AUDIO_DECODER_RELEASED -> "Audio decoder released"
        EventLogger.EVENT_AUDIO_DISABLED -> "Audio disabled"
        EventLogger.EVENT_AUDIO_SESSION_ID -> "Audio session ID"
        EventLogger.EVENT_AUDIO_ATTRIBUTES_CHANGED -> "Audio attributes changed"
        EventLogger.EVENT_SKIP_SILENCE_ENABLED_CHANGED -> "Skip silence enabled changed"
        EventLogger.EVENT_AUDIO_SINK_ERROR -> "Audio sink error"
        EventLogger.EVENT_VOLUME_CHANGED -> "Volume changed"
        EventLogger.EVENT_VIDEO_ENABLED -> "Video enabled"
        EventLogger.EVENT_VIDEO_DECODER_INITIALIZED -> "Video decoder initialized"
        EventLogger.EVENT_VIDEO_INPUT_FORMAT_CHANGED -> "Video input format changed"
        EventLogger.EVENT_DROPPED_VIDEO_FRAMES -> "Dropped video frames"
        EventLogger.EVENT_VIDEO_DECODER_RELEASED -> "Video decoder released"
        EventLogger.EVENT_VIDEO_DISABLED -> "Video disabled"
        EventLogger.EVENT_VIDEO_FRAME_PROCESSING_OFFSET -> "Video frame processing offset"
        EventLogger.EVENT_RENDERED_FIRST_FRAME -> "Rendered first frame"
        EventLogger.EVENT_VIDEO_SIZE_CHANGED -> "Video size changed"
        EventLogger.EVENT_SURFACE_SIZE_CHANGED -> "Surface size changed"
        EventLogger.EVENT_DRM_SESSION_ACQUIRED -> "DRM session acquired"
        EventLogger.EVENT_DRM_KEYS_LOADED -> "DRM keys loaded"
        EventLogger.EVENT_DRM_SESSION_MANAGER_ERROR -> "DRM session manager error"
        EventLogger.EVENT_DRM_KEYS_RESTORED -> "DRM keys restored"
        EventLogger.EVENT_DRM_KEYS_REMOVED -> "DRM keys removed"
        EventLogger.EVENT_DRM_SESSION_RELEASED -> "DRM session released"
        EventLogger.EVENT_PLAYER_RELEASED -> "Player released"
        EventLogger.EVENT_AUDIO_CODEC_ERROR -> "Audio codec error"
        EventLogger.EVENT_VIDEO_CODEC_ERROR -> "Video codec error"
        else -> "Unknown event $this"
    }
}

fun Int.commandToString(): String {
    return when (this) {
        Player.COMMAND_INVALID -> "invalid"
        Player.COMMAND_PLAY_PAUSE -> "play_pause"
        Player.COMMAND_PREPARE -> "prepare"
        Player.COMMAND_STOP -> "stop"
        Player.COMMAND_SEEK_TO_DEFAULT_POSITION -> "seek_to_default_position"
        Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM -> "seek_in_current_media_item"
        Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> "seek_to_previous_media_item"
        Player.COMMAND_SEEK_TO_PREVIOUS -> "seek_to_previous"
        Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> "seek_to_next_media_item"
        Player.COMMAND_SEEK_TO_NEXT -> "seek_to_next"
        Player.COMMAND_SEEK_TO_MEDIA_ITEM -> "seek_to_media_item"
        Player.COMMAND_SEEK_BACK -> "seek_back"
        Player.COMMAND_SEEK_FORWARD -> "seek_forward"
        Player.COMMAND_SET_SPEED_AND_PITCH -> "set_speed_and_pitch"
        Player.COMMAND_SET_SHUFFLE_MODE -> "set_shuffle_mode"
        Player.COMMAND_SET_REPEAT_MODE -> "set_repeat_mode"
        Player.COMMAND_GET_CURRENT_MEDIA_ITEM -> "get_current_media_item"
        Player.COMMAND_GET_TIMELINE -> "get_timeline"
        Player.COMMAND_GET_MEDIA_ITEMS_METADATA -> "get_media_items_metadata"
        Player.COMMAND_SET_MEDIA_ITEMS_METADATA -> "set_media_items_metadata"
        Player.COMMAND_SET_MEDIA_ITEM -> "set_media_item"
        Player.COMMAND_CHANGE_MEDIA_ITEMS -> "change_media_items"
        Player.COMMAND_GET_AUDIO_ATTRIBUTES -> "get_audio_attributes"
        Player.COMMAND_GET_VOLUME -> "get_volume"
        Player.COMMAND_GET_DEVICE_VOLUME -> "get_device_volume"
        Player.COMMAND_SET_VOLUME -> "set_volume"
        Player.COMMAND_SET_DEVICE_VOLUME -> "set_device_volume"
        Player.COMMAND_ADJUST_DEVICE_VOLUME -> "adjust_device_volume"
        Player.COMMAND_SET_VIDEO_SURFACE -> "set_video_surface"
        Player.COMMAND_GET_TEXT -> "get_text"
        Player.COMMAND_SET_TRACK_SELECTION_PARAMETERS -> "set_track_selection_parameters"
        Player.COMMAND_GET_TRACKS -> "get_tracks"
        else -> "Unknown command"
    }
}
