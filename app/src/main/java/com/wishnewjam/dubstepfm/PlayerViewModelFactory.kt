package com.wishnewjam.dubstepfm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayerViewModelFactory(
    private val radioServiceConnection: RadioServiceConnection
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(radioServiceConnection) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
