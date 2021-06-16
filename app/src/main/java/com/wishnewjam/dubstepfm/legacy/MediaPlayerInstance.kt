package com.wishnewjam.dubstepfm.legacy

import android.content.Context
import androidx.core.net.toUri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wishnewjam.dubstepfm.playback.DubstepMediaPlayer
import com.wishnewjam.dubstepfm.ui.state.UIStates

class MediaPlayerInstance(private val context: Context) : Player.Listener, DubstepMediaPlayer {
    companion object {
        private const val USER_AGENT: String = "dubstep.fm"
    }

    override var status: Int = UIStates.STATUS_UNDEFINED
    override var serviceCallback: CallbackInterface? = null

    private var currentUrl: CurrentUrl = CurrentUrl(context)
    private val mediaPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(context)
            .build()

    init {
        mediaPlayer.addListener(this)
        val attributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()
        mediaPlayer.setAudioAttributes(attributes,
                false)
        mediaPlayer.addMetadataOutput {
            if (it.length() > 0) {
                (it.get(0) as? IcyInfo?)?.title?.let { s ->
                    serviceCallback?.onMetaDataTrackChange(s)
                }
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        mediaPlayer.stop()
        Tools.logDebug { "exoPlayer: onPlayerError: error = ${error.message}" }
        status = UIStates.STATUS_ERROR
        serviceCallback?.onError("Playback error: ${error.message}")

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean,
                                      playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> notifyStatusChanged(UIStates.STATUS_LOADING)
            Player.STATE_READY     -> if (playWhenReady) {
                notifyStatusChanged(UIStates.STATUS_PLAY)
            }
            Player.STATE_ENDED     -> notifyStatusChanged(UIStates.STATUS_STOP)
            Player.STATE_IDLE      -> return
        }
        Tools.logDebug { "exoPlayer: onPlayerStateChanged: playWhenReady = $playWhenReady, playbackState = $playbackState " }
    }

    override fun callPlay() {
        Tools.logDebug { "Call play, status: $status" }
        when (status) {

            UIStates.STATUS_PLAY    -> {
                // do nothing
            }

            UIStates.STATUS_LOADING -> {
                // do nothing
            }

            else                    -> {
                play()
            }
        }
    }

    override fun callStop() {
        when (status) {
            UIStates.STATUS_PLAY -> {
                mediaPlayer.stop()
                notifyStatusChanged(UIStates.STATUS_STOP)
            }
        }
    }

//    fun changeUrl(newUrl: String) {
//        if (currentUrl.currentUrl != newUrl) {
//            currentUrl.updateUrl(newUrl)
//            play()
//        }
//    }

    private fun notifyStatusChanged(status: Int) {
        this.status = status
        serviceCallback?.onChangeStatus(status)
    }

    private fun play() {
        val source = currentUrl.currentUrl.toUri()
        val dataSourceFactory = DefaultDataSourceFactory(context,
                USER_AGENT)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(source))
        mediaPlayer.playWhenReady = true
        mediaPlayer.setMediaSource(mediaSource)
        mediaPlayer.prepare()

        notifyStatusChanged(UIStates.STATUS_LOADING)
    }

    interface CallbackInterface {
        fun onChangeStatus(status: Int)
        fun onError(error: String)
        fun onMetaDataTrackChange(trackName: String)
    }
}