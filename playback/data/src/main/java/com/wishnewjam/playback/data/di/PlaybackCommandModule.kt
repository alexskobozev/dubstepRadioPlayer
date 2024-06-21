package com.wishnewjam.playback.data.di

import com.wishnewjam.playback.data.DefaultPlaybackCommandHandler
import com.wishnewjam.playback.domain.PlaybackCommandHandler
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface PlaybackCommandModule {
    @Binds
    @Reusable
    fun playbackCommand(real : DefaultPlaybackCommandHandler) : PlaybackCommandHandler
}