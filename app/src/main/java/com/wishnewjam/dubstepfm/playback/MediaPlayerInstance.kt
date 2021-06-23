package com.wishnewjam.dubstepfm.playback

import android.content.Context
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wishnewjam.dubstepfm.legacy.CurrentUrl
import com.wishnewjam.dubstepfm.legacy.Tools
import com.wishnewjam.dubstepfm.ui.state.PlayerState

class MediaPlayerInstance(
    private val context: Context,
    private val stateChange: (state: PlayerState) -> Unit,
) : Player.Listener, DubstepMediaPlayer {
    companion object {
        private const val USER_AGENT: String = "dubstep.fm"
    }

    private var trackName: String? = null
    private var status: PlayerState = PlayerState.Undefined
    private var currentUrl: CurrentUrl = CurrentUrl(context)
    private val mediaPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(context)
        .build()
    val exoPlayer: Player = mediaPlayer

    init {
        mediaPlayer.addListener(this)
        val attributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setAudioAttributes(
            attributes,
            true
        )
        mediaPlayer.addMetadataOutput {
            if (it.length() > 0) {
                (it.get(0) as? IcyInfo?)?.title?.let { s ->
                    trackName = s
                    status.trackName = s
                    notifyStatusChanged(status)
                }
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        mediaPlayer.stop()
        Tools.logDebug { "exoPlayer: onPlayerError: error = ${error.message}" }
        notifyStatusChanged(PlayerState.Error(errorText = "${error.message}"))
    }

    override fun onPlayerStateChanged(
        playWhenReady: Boolean,
        playbackState: Int,
    ) {
        when (playbackState) {
            Player.STATE_BUFFERING -> notifyStatusChanged(PlayerState.Buffering)
            Player.STATE_READY -> {
                val state =
                    if (playWhenReady) {
                        PlayerState.Play
                    } else {
                        PlayerState.Pause
                    }
                state.trackName = trackName
                notifyStatusChanged(state)
            }
            Player.STATE_IDLE, Player.STATE_ENDED -> {
            }
        }
        Tools.logDebug { "exoPlayer: onPlayerStateChanged: playWhenReady = $playWhenReady, playbackState = $playbackState " }
    }

    override fun callPlay() {
        mediaPlayer.setWakeMode(C.WAKE_MODE_NETWORK)
        Tools.logDebug { "Call play, status: $status" }
        if (status !is PlayerState.Play && status != PlayerState.Buffering) {
            play()
        }
    }

    override fun callStop() {
        mediaPlayer.setWakeMode(C.WAKE_MODE_NONE)
        if (status is PlayerState.Play) {
            mediaPlayer.stop()
            status = PlayerState.Pause
        }
        trackName = null
    }

//    fun changeUrl(newUrl: String) {
//        if (currentUrl.currentUrl != newUrl) {
//            currentUrl.updateUrl(newUrl)
//            play()
//        }
//    }

    private fun notifyStatusChanged(status: PlayerState) {
        if (this.status == status && this.status.trackName == status.trackName) return
        this.status = status
        stateChange.invoke(status)
    }

    private fun play() {
        trackName = null
        val source = currentUrl.currentUrl.toUri()
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            USER_AGENT
        )
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(source))
        mediaPlayer.playWhenReady = true
        mediaPlayer.setMediaSource(mediaSource)
        mediaPlayer.prepare()
    }

    fun destroy() {
        trackName = null
        mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return status == PlayerState.Play
    }
}