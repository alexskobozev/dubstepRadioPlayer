package com.wishnewjam.stream.domain

import com.wishnewjam.di.Api

interface StreamApi : Api {
    val streamRepository: StreamRepository
}
