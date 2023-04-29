package com.wishnewjam.dubstepfm

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class ChooseBitrateDialogFragment : androidx.fragment.app.DialogFragment(),
        View.OnClickListener {

    private var items: ArrayList<TextView> = ArrayList()
    private lateinit var mediaViewModel: MediaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialogfragment_bitrate, container,
                false)
        items = arrayListOf(v.findViewById(R.id.tv_bitrate_24), v.findViewById(R.id.tv_bitrate_64), v.findViewById(R.id.tv_bitrate_128),
            v.findViewById(R.id.tv_bitrate_256))
        for (item in items) {
            item.setOnClickListener(this)
        }
        mediaViewModel = ViewModelProviders.of(this)
                .get(MediaViewModel::class.java)
        // TODO:
        // mediaViewModel.currentUrl.observe(this,
        //         Observer<String> { t -> t?.let { colorize(it) } })

        v.findViewById<CheckBox>(R.id.chb_consent).isChecked = mediaViewModel.userConsent.value ?: true
        v.findViewById<CheckBox>(R.id.chb_consent).setOnCheckedChangeListener { _, isChecked ->
            mediaViewModel.changeConsent(isChecked)
        }

        v.findViewById<TextView>(R.id.tv_privacy_policy).setOnClickListener {
            startActivity(
                    Intent(Intent.ACTION_VIEW, Links.PRIVACY_POLICY.toUri()))
        }
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
        mediaViewModel.changeBitrate(br)
        dismiss()
    }

    private fun colorize(which: String) {
        colorize(Links.AS_ARRAY.indexOf(which))
    }

    private fun colorize(which: Int) {
        for ((index, value) in items.withIndex()) {
            if (index == which) {
                value.setTextColor(ResourcesCompat.getColor(resources,
                        R.color.text_color_secondary, null))
            }
            else {
                value.setTextColor(Color.WHITE)
            }
        }
    }
}
