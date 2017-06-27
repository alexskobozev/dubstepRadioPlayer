package com.wishnewjam.dubstepfm.dagger

import com.wishnewjam.dubstepfm.ChooseBitrateDialogFragment
import com.wishnewjam.dubstepfm.MainActivity
import com.wishnewjam.dubstepfm.MainService
import com.wishnewjam.dubstepfm.MyApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidModule::class))
interface ApplicationComponent {

    fun inject(application: MyApplication)

    fun inject(mainService: MainService)

    fun inject(dialog: ChooseBitrateDialogFragment)
}