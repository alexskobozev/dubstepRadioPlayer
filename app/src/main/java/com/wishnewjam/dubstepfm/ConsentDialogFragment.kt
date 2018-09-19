package com.wishnewjam.dubstepfm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProviders

class ConsentDialogFragment : AppCompatDialogFragment() {

    private lateinit var mediaViewModel: MediaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.dialogfragment_consent, container, false)
        mediaViewModel = ViewModelProviders.of(this).get(MediaViewModel::class.java)
        return v

    }
}