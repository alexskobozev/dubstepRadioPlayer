package com.wishnewjam.dubstepfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {

    private lateinit var radioServiceConnection: RadioServiceConnection
    private val playerViewModel: PlayerViewModel by viewModels {
        // Provide the radioServiceConnection to the ViewModel
        PlayerViewModelFactory(radioServiceConnection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the radioServiceConnection
        radioServiceConnection = RadioServiceConnection(applicationContext)

        setContent {
            DubstepFMRadioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    PlayerScreen(viewModel = playerViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unbind the service when the activity is destroyed
        if (::radioServiceConnection.isInitialized) {
            radioServiceConnection.unbindService()
        }
    }
}
