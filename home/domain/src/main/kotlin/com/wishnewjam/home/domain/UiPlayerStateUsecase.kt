package com.wishnewjam.home.domain

import kotlinx.coroutines.flow.Flow

interface UiPlayerStateUsecase<T> {
    val currentState: Flow<T>
}