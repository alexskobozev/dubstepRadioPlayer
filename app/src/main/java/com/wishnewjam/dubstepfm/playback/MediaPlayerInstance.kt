//package com.wishnewjam.dubstepfm.playback
//
//import android.content.Context
//import android.net.Uri
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.audio.AudioAttributes
//import com.google.android.exoplayer2.metadata.icy.IcyInfo
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.wishnewjam.dubstepfm.legacy.Tools
//import com.wishnewjam.dubstepfm.ui.state.PlayerState
//import kotlinx.coroutines.flow.callbackFlow
//
//class MediaPlayerInstance(
//    private val context: Context,
//    private val trackNameChange: (trackName: String) -> Unit,
//    private val stateChange: (state: PlayerState) -> Unit,
//) : DubstepMediaPlayer,
//    Player.Listener {
//
//    private var status: PlayerState = PlayerState.Undefined
//    private val mediaPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(context)
//        .build()
//    val exoPlayer: Player = mediaPlayer
//
//    init {
//        mediaPlayer.addListener(this)
//        val attributes = AudioAttributes.Builder()
//            .setUsage(C.USAGE_MEDIA)
//            .setContentType(C.CONTENT_TYPE_MUSIC)
//            .build()
//        mediaPlayer.setAudioAttributes(
//            attributes,
//            true
//        )
//        mediaPlayer.addMetadataOutput {
//            if (it.length() > 0) {
//                (it.get(0) as? IcyInfo?)?.title?.let { s ->
//                    trackNameChange.invoke(s)
//                }
//            }
//        }
//    }
//
//    override fun onPlayerError(error: PlaybackException) {
//        mediaPlayer.stop()
//        Tools.logDebug { "exoPlayer: onPlayerError: error = ${error.message}" }
//        notifyStatusChanged(PlayerState.Error(errorText = "${error.message}"))
//    }
//
//    override fun onPlayerStateChanged(
//        playWhenReady: Boolean,
//        playbackState: Int,
//    ) {
//        when (playbackState) {
//            Player.STATE_BUFFERING -> notifyStatusChanged(PlayerState.Buffering)
//            Player.STATE_READY -> {
//                val state =
//                    if (playWhenReady) {
//                        PlayerState.Play
//                    } else {
//                        PlayerState.Pause
//                    }
//                notifyStatusChanged(state)
//            }
//            Player.STATE_IDLE, Player.STATE_ENDED -> {
//            }
//        }
//        Tools.logDebug { "exoPlayer: onPlayerStateChanged: playWhenReady = $playWhenReady, playbackState = $playbackState " }
//    }
//
//    override fun callPlay(uri: Uri) {
//        mediaPlayer.setWakeMode(C.WAKE_MODE_NETWORK)
//        Tools.logDebug { "Call play, status: $status uri $uri" }
//        play(uri)
//    }
//
//    override fun callStop() {
//        mediaPlayer.setWakeMode(C.WAKE_MODE_NONE)
//        if (status is PlayerState.Play) {
//            mediaPlayer.stop()
//            status = PlayerState.Pause
//        }
//    }
//
//    private fun notifyStatusChanged(status: PlayerState) {
//        if (this.status == status) return
//        this.status = status
//        stateChange.invoke(status)
//    }
//
//    private fun play(uri: Uri) {
//        val dataSourceFactory = DefaultDataSourceFactory(
//            context,
//            null
//        )
//        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(MediaItem.fromUri(uri))
//        mediaPlayer.playWhenReady = true
//        mediaPlayer.setMediaSource(mediaSource)
//        mediaPlayer.prepare()
//    }
//
//    fun destroy() {
//        mediaPlayer.release()
//    }
//
//    fun isPlaying(): Boolean {
//        return status == PlayerState.Play
//    }
//}