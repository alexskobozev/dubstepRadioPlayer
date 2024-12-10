package com.wishnewjam.dubstepfm

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.extractor.metadata.icy.IcyInfo

@UnstableApi
class MediaPlayerInstance(private val context: Context) : Player.Listener {

    companion object {
        private const val USER_AGENT: String = "dubstep.fm"
    }

    var status: Int = UIStates.STATUS_UNDEFINED
    var serviceCallback: CallbackInterface? = null
    private var currentUrl: CurrentUrl = CurrentUrl(context)

    private val mediaPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        addListener(this@MediaPlayerInstance)
        val attributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        setAudioAttributes(attributes, true)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        // This callback replaces the old onPlayerStateChanged.
        // Here we can check mediaPlayer.playWhenReady for readiness and handle states.
        val playWhenReady = mediaPlayer.playWhenReady
        when (playbackState) {
            Player.STATE_BUFFERING -> notifyStatusChanged(UIStates.STATUS_LOADING)
            Player.STATE_READY -> if (playWhenReady) notifyStatusChanged(UIStates.STATUS_PLAY)
            Player.STATE_ENDED -> notifyStatusChanged(UIStates.STATUS_STOP)
            Player.STATE_IDLE -> {
                // no action needed
            }
        }
        Tools.logDebug {
            "media3Player: onPlaybackStateChanged: playWhenReady = $playWhenReady, playbackState = $playbackState"
        }
    }

    override fun onMetadata(metadata: Metadata) {
        // Media3: onMetadata is now part of Player.Listener
        if (metadata.length() > 0) {
            for (i in 0 until metadata.length()) {
                val entry = metadata[i]
                if (entry is IcyInfo) {
                    entry.title?.let { title ->
                        serviceCallback?.onMetaDataTrackChange(title)
                    }
                }
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Tools.logDebug { "media3Player: onPlayerError: error = $error" }
        status = UIStates.STATUS_ERROR
        serviceCallback?.onError("Playback error: $error")
    }

    override fun onTracksChanged(tracks: Tracks) {
        Tools.logDebug { "media3Player: onTracksChanged: tracks = $tracks" }
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        Tools.logDebug { "media3Player: onTimelineChanged: timeline = $timeline" }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        Tools.logDebug { "media3Player: onIsLoadingChanged: isLoading = $isLoading" }
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        Tools.logDebug { "media3Player: onPlaybackParametersChanged: $playbackParameters" }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        Tools.logDebug { "media3Player: onRepeatModeChanged: $repeatMode" }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        Tools.logDebug { "media3Player: onPositionDiscontinuity" }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        Tools.logDebug { "media3Player: onShuffleModeEnabledChanged: $shuffleModeEnabled" }
    }

    fun callPlay() {
        Tools.logDebug { "Call play, status: $status" }
        when (status) {
            UIStates.STATUS_PLAY -> {
                // do nothing
            }
            UIStates.STATUS_LOADING -> {
                // do nothing
            }
            else -> {
                play()
            }
        }
    }

    fun callStop() {
        when (status) {
            UIStates.STATUS_PLAY -> {
                mediaPlayer.stop()
                notifyStatusChanged(UIStates.STATUS_STOP)
            }
        }
    }

    fun changeUrl(newUrl: String) {
        if (currentUrl.currentUrl != newUrl) {
            currentUrl.updateUrl(newUrl)
            play()
        }
    }

    private fun notifyStatusChanged(status: Int) {
        this.status = status
        serviceCallback?.onChangeStatus(status)
    }

    private fun play() {
        val sourceUri = currentUrl.currentUrl.toUri()
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(sourceUri))

        mediaPlayer.setMediaSource(mediaSource)
        mediaPlayer.prepare()
        mediaPlayer.playWhenReady = true

        notifyStatusChanged(UIStates.STATUS_LOADING)
    }

    fun getPlayer(): Player {
        return mediaPlayer
    }

    interface CallbackInterface {
        fun onChangeStatus(status: Int)
        fun onError(error: String)
        fun onMetaDataTrackChange(trackName: String)
    }
}
