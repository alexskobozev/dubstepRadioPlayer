package com.wishnewjam.dubstepfm.presentation.main

import android.net.Uri

data class MainState(
    val isLoading: Boolean,
    val bitratesList: List<Int>?,
    val activeListElementNum: Int?,
    val currentStreamUri: Uri?
)