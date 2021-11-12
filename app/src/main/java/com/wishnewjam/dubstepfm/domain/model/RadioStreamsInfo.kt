package com.wishnewjam.dubstepfm.domain.model

import android.net.Uri

data class RadioStreamsInfo(val bitratesList: List<Int>, val activeListElementNum: Int, val currentStreamUri: Uri)