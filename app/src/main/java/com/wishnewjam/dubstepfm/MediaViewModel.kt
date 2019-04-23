package com.wishnewjam.dubstepfm

import android.app.Application
import android.preference.PreferenceManager
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    var mediaPlayerInstance: MediaPlayerInstance =
            (application as MyApplication).mediaPlayerInstance

    val currentUrl = MutableLiveData<String>()
    val userConsent = MutableLiveData<Boolean>()
    var consentDialogShown = false

    init {
        val defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(application)
        currentUrl.value =
                defaultSharedPreferences.getString(MainService.SP_KEY_BITRATE,
                        Links.LINK_128)
        userConsent.value =
                defaultSharedPreferences.getBoolean(MainService.SP_KEY_CONSENT,
                        false)
        consentDialogShown = defaultSharedPreferences.getBoolean(
                MainService.SP_KEY_CONSENT_DIALOG_SHOWN, false)
    }

    fun changeBitrate(br: String) {
        mediaPlayerInstance.changeUrl(br)
        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit {
                    putString(MainService.SP_KEY_BITRATE, br)
                }
        currentUrl.value = br
    }

    fun changeConsent(b: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit {
                    putBoolean(MainService.SP_KEY_CONSENT, b)
                }
        userConsent.value = b
    }

    fun setDialogShown() {
        PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit {
                    putBoolean(MainService.SP_KEY_CONSENT_DIALOG_SHOWN, true)
                }
        consentDialogShown = true
    }
}