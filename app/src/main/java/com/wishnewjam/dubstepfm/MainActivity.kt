package com.wishnewjam.dubstepfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels {
        // Provide the radioServiceConnection to the ViewModel
        PlayerViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DubstepFMRadioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    PlayerScreen(viewModel = playerViewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.onStart(this)
    }
}
