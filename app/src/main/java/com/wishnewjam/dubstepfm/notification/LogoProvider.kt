package com.wishnewjam.dubstepfm.notification

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.legacy.Tools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LogoProvider(resources: Resources, scope: CoroutineScope) {

    private var bitmapLogo: Bitmap? = null

    init {
        scope.launch {
            bitmapLogo = decodeLogo(resources)
        }
    }

    private suspend fun decodeLogo(resources: Resources): Bitmap = suspendCoroutine {
        try {
            bitmapLogo = BitmapFactory.decodeResource(
                resources,
                R.drawable.logo_screen
            )
            it.resume(bitmapLogo!!)
        } catch (t: Throwable) {
            Tools.logDebug { "exception while decoding logo img: ${t.message}" }
            it.resumeWithException(t)
        }
    }

    fun getLogoBitmap(): Bitmap? {
        return bitmapLogo
    }

}