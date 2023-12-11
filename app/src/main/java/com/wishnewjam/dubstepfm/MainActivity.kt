package com.wishnewjam.dubstepfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {

    private val playerViewModel: com.wishnewjam.home.presentation.PlayerViewModel by viewModels {
        // Provide the radioServiceConnection to the ViewModel
        com.wishnewjam.home.presentation.PlayerViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        playerViewModel.onStart(this)
    }
}
