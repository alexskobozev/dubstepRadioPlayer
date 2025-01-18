package com.wishnewjam.stream.domain.model

sealed class RadioStream(
    val description: String,
    val url: String,
    val id: Int,
) {
    class RadioStream24 :
        RadioStream(description = "24", url = "http://stream.dubstep.fm/24mp3", id = 24)

    class RadioStream64 :
        RadioStream(description = "64", url = "http://stream.dubstep.fm/64mp3", id = 64)

    class RadioStream128 :
        RadioStream(description = "128", url = "http://stream.dubstep.fm/128mp3", id = 128)

    class RadioStream256 :
        RadioStream(description = "256", url = "http://stream.dubstep.fm/256mp3", id = 256)
}
