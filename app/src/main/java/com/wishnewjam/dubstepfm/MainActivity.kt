package com.wishnewjam.dubstepfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.wishnewjam.commons.android.apiContainer
import com.wishnewjam.di.getFeature
import com.wishnewjam.dubstepfm.di.DaggerMainActivityComponent
import com.wishnewjam.home.domain.PlayerViewModel
import com.wishnewjam.home.domain.PlayerViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: PlayerViewModelFactory

    private val playerViewModel: PlayerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMainActivityComponent.factory().create(
            apiContainer().getFeature(),
            apiContainer().getFeature()
        ).inject(this)
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
        throw Exception("uncomment and do something here")
//        // TODO: move out of here
//        val sessionToken = SessionToken(
//            context,
//            ComponentName(context, RadioService::class.java)
//        )
//        Timber.d("Building MediaController")
//        val controllerFuture =
//            MediaController.Builder(context, sessionToken).buildAsync()
//        controllerFuture.addListener(
//            {
//                Timber.d("MediaController instance initiated")
//                player = controllerFuture.get()
//                // todo no need here, need to listen metadata repository for metadata
//                _nowPlayingText.value = player!!.mediaMetadata.title?.toString() ?: "no data"
//            },
//            MoreExecutors.directExecutor()
//        )
    }
}
