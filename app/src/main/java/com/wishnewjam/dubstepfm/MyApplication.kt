package com.wishnewjam.dubstepfm

import android.app.Application
import com.wishnewjam.di.Api
import com.wishnewjam.di.ApiContainer
import com.wishnewjam.dubstepfm.di.Apis
import com.wishnewjam.dubstepfm.di.DaggerAppComponent
import com.wishnewjam.dubstepfm.di.LibsModule
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class MyApplication : Application(), ApiContainer {

    @Inject
    @Apis
    lateinit var apiMap: Map<Class<*>, @JvmSuppressWildcards Provider<Api>>

    @Suppress("Unchecked_Cast")
    override fun <T> getFeature(key: Class<T>): T = apiMap[key]!!.get() as T

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        DaggerAppComponent.factory()
            .create(LibsModule(application = this))
            .inject(this)
    }
}
