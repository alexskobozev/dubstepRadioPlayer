package com.wishnewjam.playback.presentation

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.wishnewjam.commons.android.apiContainer
import com.wishnewjam.di.getFeature
import com.wishnewjam.playback.domain.RadioServiceController
import com.wishnewjam.playback.presentation.di.DaggerRadioServiceComponent
import javax.inject.Inject

class RadioService : MediaSessionService() {

    @Inject
    lateinit var radioServiceController: RadioServiceController

    override fun onCreate() {
        DaggerRadioServiceComponent.factory().create(
            playbackApi = apiContainer().getFeature(),
        ).inject(this)
        super.onCreate()
        radioServiceController.create(this)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) =
        radioServiceController.getSession()
}
