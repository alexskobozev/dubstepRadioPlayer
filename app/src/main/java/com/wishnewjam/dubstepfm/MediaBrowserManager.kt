package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors

class MediaBrowserManager(
    private val context: Context,
    private val callback: Callback
) {

    interface Callback {
        fun onConnected()
        fun onPlaybackStateChanged(isPlaying: Boolean, playbackState: Int)
        fun onMediaMetadataChanged(metadata: MediaMetadata)
        fun onError(error: Throwable)
    }

    private var mediaBrowser: MediaBrowser? = null

    // Player.Listener to handle media events.
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val browser = mediaBrowser ?: return
            val isPlaying = browser.playWhenReady && playbackState == Player.STATE_READY
            callback.onPlaybackStateChanged(isPlaying, playbackState)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            callback.onMediaMetadataChanged(mediaMetadata)
        }

        override fun onPlayerError(error: PlaybackException) {
            callback.onError(error)
        }
    }

    fun initialize() {
        val sessionToken = SessionToken(context, ComponentName(context, MainService::class.java))
        val mediaBrowserFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()

        Futures.addCallback(
            mediaBrowserFuture,
            object : com.google.common.util.concurrent.FutureCallback<MediaBrowser> {
                override fun onSuccess(result: MediaBrowser?) {
                    if (result == null) {
                        callback.onError(IllegalStateException("MediaBrowser is null after building."))
                        return
                    }
                    mediaBrowser = result
                    // The browser is now connected.
                    mediaBrowser?.addListener(playerListener)
                    callback.onConnected()
                }

                override fun onFailure(t: Throwable) {
                    callback.onError(t)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun release() {
        mediaBrowser?.removeListener(playerListener)
        mediaBrowser?.release()
        mediaBrowser = null
    }

    fun play() {
        mediaBrowser?.play()
    }

    fun stop() {
        mediaBrowser?.stop()
    }

    fun isPlaying(): Boolean {
        val browser = mediaBrowser ?: return false
        return browser.playWhenReady && browser.playbackState == Player.STATE_READY
    }

    fun getPlaybackState(): Int {
        return mediaBrowser?.playbackState ?: Player.STATE_IDLE
    }

    fun getCurrentMetadata(): MediaMetadata? {
        return mediaBrowser?.mediaMetadata
    }

    fun startService() {
        context.startService(Intent(context, MainService::class.java))
    }
}
