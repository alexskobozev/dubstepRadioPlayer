package com.wishnewjam.home.domain

import com.wishnewjam.di.Api

interface HomeApi : Api {
    val metadataUsecase: MetadataUsecase
    val homeViewmodelFactory: PlayerViewModelFactory
}