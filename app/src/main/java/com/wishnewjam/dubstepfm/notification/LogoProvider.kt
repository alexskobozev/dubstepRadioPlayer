package com.wishnewjam.dubstepfm.notification

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.MediaMetadataCompat
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.legacy.Tools
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogoProvider(resources: Resources, scope: CoroutineScope, dispatcher: CoroutineDispatcher) {

    private var bitmapLogo: Bitmap? = null

    init {
        scope.launch {
            decodeLogo(resources, dispatcher)
        }
    }

    private suspend fun decodeLogo(resources: Resources, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            try {
                bitmapLogo = BitmapFactory.decodeResource(resources,
                        R.drawable.logo_screen)
            } catch (t: Throwable) {
                Tools.logDebug { "exception while decoding logo img: ${t.message}" }
            }
        }
    }


    fun updateMetadataBuilderWithLogo(builder: MediaMetadataCompat.Builder) {
        if (bitmapLogo == null) {
            return
        }
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmapLogo)
    }

}