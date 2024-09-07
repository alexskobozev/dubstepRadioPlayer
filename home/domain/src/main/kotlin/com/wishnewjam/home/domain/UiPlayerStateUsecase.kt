package com.wishnewjam.home.domain

import kotlinx.coroutines.flow.StateFlow

interface UiPlayerStateUsecase<T> {
    val currentState: StateFlow<T>
}