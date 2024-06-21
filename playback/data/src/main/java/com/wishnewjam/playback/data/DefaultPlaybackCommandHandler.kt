package com.wishnewjam.playback.data

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.wishnewjam.playback.domain.PlaybackCommandHandler
import timber.log.Timber
import javax.inject.Inject

class DefaultPlaybackCommandHandler @Inject constructor() : PlaybackCommandHandler {
    private var player: MediaController? = null

    override fun init(context: Context, serviceClass: Class<out MediaSessionService>) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, serviceClass)
        )
        Timber.d("Building MediaController")
        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                Timber.d("MediaController instance initiated")
                player = controllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun play() {
        player?.play()
    }

    override fun stop() {
        player?.stop()
    }
}