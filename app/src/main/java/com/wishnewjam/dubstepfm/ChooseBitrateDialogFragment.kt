package com.wishnewjam.dubstepfm

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import javax.inject.Inject

class ChooseBitrateDialogFragment : DialogFragment() {

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MyApplication.graph.inject(this)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.choose_bitrate)
                .setItems(R.array.bitrates) { _, which -> changeBitrate(which) }
        return builder.create()
    }

    private fun changeBitrate(which: Int) {
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_24).apply()
        when (which) {
            0 -> {
                mediaPlayerInstance.changeUrl(Links.LINK_24)
                dismiss()
            }
            1 -> {
                mediaPlayerInstance.changeUrl(Links.LINK_64)
                dismiss()
            }
            2 -> {
                mediaPlayerInstance.changeUrl(Links.LINK_128)
                dismiss()
            }
            3 -> {
                mediaPlayerInstance.changeUrl(Links.LINK_256)
                dismiss()
            }
        }
    }
}
