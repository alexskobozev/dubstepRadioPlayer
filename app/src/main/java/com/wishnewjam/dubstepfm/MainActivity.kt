package com.wishnewjam.dubstepfm

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.wishnewjam.commons.android.apiContainer
import com.wishnewjam.di.getFeature
import com.wishnewjam.dubstepfm.di.DaggerMainActivityComponent
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.home.domain.PlayerViewModelFactory
import com.wishnewjam.playback.domain.PlaybackCommandHandler
import com.wishnewjam.playback.presentation.RadioService
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: PlayerViewModelFactory

    @Inject
    lateinit var playbackCommandHandler: PlaybackCommandHandler

    private val playerViewModel: PlayerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMainActivityComponent.factory().create(
            homeApi = apiContainer().getFeature(),
            metadataApi = apiContainer().getFeature(),
            playbackApi = apiContainer().getFeature(),
        ).inject(this)
        actionBar?.hide()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            com.wishnewjam.commons.design.DubstepFMRadioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    com.wishnewjam.home.presentation.PlayerScreen(viewModel = playerViewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playbackCommandHandler.init(this, RadioService::class.java)
    }
}
