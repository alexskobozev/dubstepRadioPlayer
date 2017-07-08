package com.wishnewjam.dubstepfm

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TrackInfoDialogFragment : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.dialogfragment_info, container, false)

        val tv : TextView? = v?.findViewById(R.id.tv_trackname)

        tv?.text = arguments.getString(TAG_TRACKNAME)

        return v
    }

    companion object {

        private val TAG_TRACKNAME = "trackname"
        private val TAG_LINK = "link"


        fun newInstance(trackname: String,
                        link: String): DialogFragment {
            val bundle = Bundle()
            bundle.putString(TAG_TRACKNAME, trackname)
            bundle.putString(TAG_LINK, link)
            val dialogFragment = DialogFragment()
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

}
