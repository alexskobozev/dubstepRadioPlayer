package com.wishnewjam.dubstepfm

import android.content.Context
import android.util.Log
import android.widget.Toast
import timber.log.Timber

object Tools {
    fun logDebug(lambda: () -> String) {
        Timber.d(lambda())
    }

    fun toastDebug(lambda: () -> String, context: Context) {
    }
}