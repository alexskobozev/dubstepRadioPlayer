package com.wishnewjam.playback.domain

import com.wishnewjam.di.Api

interface PlaybackApi : Api {
    val radioServiceController: RadioServiceController
    val playbackCommandHandler: PlaybackCommandHandler
}