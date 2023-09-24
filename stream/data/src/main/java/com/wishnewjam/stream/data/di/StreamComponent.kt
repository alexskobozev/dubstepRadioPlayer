package com.wishnewjam.stream.data.di

import com.wishnewjam.stream.domain.StreamApi
import dagger.Component

@Component(
    modules = [
        StreamModule::class,
    ],
)
interface StreamComponent : StreamApi
