package com.wishnewjam.dubstepfm.data

import android.net.Uri

sealed class RadioStream(val bitrate: Int) {
    val name: String = "$bitrate kbps"
    val uri: Uri = Uri.parse("http://stream.dubstep.fm/${bitrate}mp3")

    class RadioStream24 : RadioStream(24)
    class RadioStream64 : RadioStream(64)
    class RadioStream128 : RadioStream(128)
    class RadioStream256 : RadioStream(256)
    class RadioStreamUnknown : RadioStream(0)

    companion object{
        val default = RadioStream128()
        val unknown = RadioStream128()
    }
}