package com.wishnewjam.dubstepfm

import android.content.Context

class CurrentUrl(private val context: Context) {
    companion object {
        const val SP_KEY: String = "dubstepfm"
        const val URL_KEY = "currentUrl"
    }

    var currentUrl: String = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).getString(URL_KEY, Links.LINK_128)
            ?: Links.LINK_128

    fun updateUrl(url: String) {
        currentUrl = url
        context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).edit().putString(URL_KEY, url).apply()
    }
}