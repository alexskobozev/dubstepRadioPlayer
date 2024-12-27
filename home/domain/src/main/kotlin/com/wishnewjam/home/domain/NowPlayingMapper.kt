package com.wishnewjam.home.domain

import com.wishnewjam.home.domain.model.NowPlayingText

interface NowPlayingMapper {
    fun nowPlaying(str: String): NowPlayingText
}