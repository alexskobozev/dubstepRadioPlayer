package com.wishnewjam.dubstepfm.di

import com.wishnewjam.dubstepfm.MyApplication
import dagger.Component

@Component(
    modules = [
        ApiModule::class,
    ]
)
interface AppComponent {

    fun inject(target: MyApplication)

    @Component.Factory
    interface Factory {
        fun create(libsModule: LibsModule): AppComponent
    }
}
