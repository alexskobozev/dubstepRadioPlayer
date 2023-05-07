package com.wishnewjam.dubstepfm.di

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.wishnewjam.di.ApiContainer

fun Activity.apiContainer(): ApiContainer = application as ApiContainer
fun Fragment.apiContainer(): ApiContainer = requireActivity().apiContainer()
fun Context.apiContainer(): ApiContainer =
    this.applicationContext as ApiContainer

fun View.apiContainer(): ApiContainer = context.apiContainer()
