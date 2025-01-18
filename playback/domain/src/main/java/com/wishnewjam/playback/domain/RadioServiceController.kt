package com.wishnewjam.playback.domain

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

interface RadioServiceController {
    fun create(mediaSessionService: MediaSessionService)
    fun destroy()
    fun getSession(): MediaSession?
}