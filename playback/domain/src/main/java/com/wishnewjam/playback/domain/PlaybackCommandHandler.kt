package com.wishnewjam.playback.domain

import android.content.Context
import androidx.media3.session.MediaSessionService

interface PlaybackCommandHandler {
    fun init(context: Context, serviceClass: Class<out MediaSessionService>)
    fun play()
    fun pause()
}