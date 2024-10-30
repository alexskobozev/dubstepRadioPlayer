package com.wishnewjam.dubstepfm

import android.app.Application
import android.preference.PreferenceManager
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaPlayerInstance: MediaPlayerInstance =
            (application as MyApplication).mediaPlayerInstance

    val currentUrl = MutableLiveData<String>()

    init {
        val defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(application)
        currentUrl.value =
                defaultSharedPreferences.getString(MainService.SP_KEY_BITRATE,
                        Links.LINK_128)
    }

    fun changeBitrate(br: String) {
        mediaPlayerInstance.changeUrl(br)
        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit {
                    putString(MainService.SP_KEY_BITRATE, br)
                }
        currentUrl.value = br
    }
}