package com.wishnewjam.dubstepfm

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import javax.inject.Inject


class ChooseBitrateDialogFragment : DialogFragment(), View.OnClickListener {

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance

    private var items: ArrayList<TextView> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MyApplication.graph.inject(this)
        val v = inflater.inflate(R.layout.dialogfragment_bitrate, container, false)
        items = arrayListOf(v.findViewById(R.id.tv_bitrate_24),
                v.findViewById(R.id.tv_bitrate_64),
                v.findViewById(R.id.tv_bitrate_128),
                v.findViewById(R.id.tv_bitrate_256))
        for (item in items) {
            item.setOnClickListener(this)
        }
        val initial = PreferenceManager.getDefaultSharedPreferences(activity).getString(MainService.SP_KEY_BITRATE, Links.LINK_128)
        colorize(Links.AS_ARRAY.indexOf(initial))
        return v
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_bitrate_24 -> {
                changeBitrate(0)
            }
            R.id.tv_bitrate_64 -> {
                changeBitrate(1)
            }
            R.id.tv_bitrate_128 -> {
                changeBitrate(2)
            }
            R.id.tv_bitrate_256 -> {
                changeBitrate(3)
            }
        }
    }

    private fun changeBitrate(which: Int) {
        val br: String = Links.AS_ARRAY[which]
        colorize(which)

        mediaPlayerInstance.changeUrl(br)
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit().putString(MainService.SP_KEY_BITRATE, br).apply()
        dismiss()

    }

    private fun colorize(which: Int) {
        for ((index, value) in items.withIndex()) {
            if (index == which) {
                value.setTextColor(ResourcesCompat.getColor(resources, R.color.text_color_secondary, null))
            } else {
                value.setTextColor(Color.WHITE)
            }
        }
    }
}
