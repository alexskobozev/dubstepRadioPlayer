package com.wishnewjam.commons.android

import android.app.Activity
import android.app.Service
import com.wishnewjam.di.ApiContainer

fun Activity.apiContainer(): ApiContainer = application as ApiContainer
fun Service.apiContainer(): ApiContainer = application as ApiContainer
