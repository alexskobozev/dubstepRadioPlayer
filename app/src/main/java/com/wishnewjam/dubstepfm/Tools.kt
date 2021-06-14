package com.wishnewjam.dubstepfm

import android.content.Context
import android.util.Log
import android.widget.Toast

object Tools {
    inline fun logDebug(lambda: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d("Dubstep.fm.player", lambda())
        }
    }

    inline fun toastDebug(lambda: () -> String, context: Context) {
        if (BuildConfig.DEBUG){
            Toast.makeText(context, lambda(), Toast.LENGTH_LONG).show()

        }
    }
}