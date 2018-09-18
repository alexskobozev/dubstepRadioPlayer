package com.wishnewjam.dubstepfm

internal object Links {
    const val LINK_128 = "http://stream.dubstep.fm/128mp3"
    private const val LINK_256 = "http://stream.dubstep.fm/256mp3"
    private const val LINK_64 = "http://stream.dubstep.fm/64mp3"
    private const val LINK_24 = "http://stream.dubstep.fm/24mp3"

    val AS_ARRAY = arrayListOf(LINK_24, LINK_64, LINK_128, LINK_256)
}
