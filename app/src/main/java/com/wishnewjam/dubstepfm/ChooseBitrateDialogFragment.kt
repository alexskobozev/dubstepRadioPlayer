package com.wishnewjam.dubstepfm

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class ChooseBitrateDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.choose_bitrate)
                .setItems(R.array.bitrates) { dialog, which -> changeBitrate(which) }
        return builder.create()
    }

    private fun changeBitrate(which: Int) {
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_24).apply()
        when (which) {
            0 -> {
                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_24).apply()
                dismiss()
            }
            1 -> {
                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_64).apply()
                dismiss()
            }
            2 -> {
                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_128).apply()
                dismiss()
            }
            3 -> {
                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_256).apply()
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val activity = activity
        if (activity is DialogInterface.OnDismissListener) {
            activity.onDismiss(dialog)
        }
    }
}
