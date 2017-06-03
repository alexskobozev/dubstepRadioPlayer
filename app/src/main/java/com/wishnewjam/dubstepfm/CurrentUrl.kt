package com.wishnewjam.dubstepfm

import android.content.Context

class CurrentUrl(private val context: Context) {
    val SP_KEY: String = "dubstepfm"
    val URL_KEY = "currentUrl"

    var currentUrl: String = context
            .getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
            .getString(URL_KEY, Links.LINK_128)

    fun updateUrl(url: String) {
        currentUrl = url
        context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
                .edit()
                .putString(URL_KEY, url).apply()
    }
}