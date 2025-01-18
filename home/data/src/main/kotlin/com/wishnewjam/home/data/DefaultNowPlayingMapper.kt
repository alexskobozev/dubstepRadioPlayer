package com.wishnewjam.home.data

import com.wishnewjam.home.domain.NowPlayingMapper
import com.wishnewjam.home.domain.model.NowPlayingText
import javax.inject.Inject

class DefaultNowPlayingMapper @Inject constructor() : NowPlayingMapper {
    override fun nowPlaying(str: String): NowPlayingText {
        val regex = Regex("""ARCHIVE\s-\s(\d{4}-\d{2}-\d{2})\s-\s(.+)""")
        val matchResult = kotlin.runCatching { regex.find(str) }.getOrNull()
        return if (matchResult != null) {
            val (date, name) = matchResult.destructured
            NowPlayingText(
                nowPlaying = name,
                year = date,
            )
        } else {
            NowPlayingText(nowPlaying = str, year = "")
        }
    }
}