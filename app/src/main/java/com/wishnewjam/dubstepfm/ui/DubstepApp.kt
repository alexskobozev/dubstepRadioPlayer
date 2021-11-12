package com.wishnewjam.dubstepfm.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.wishnewjam.dubstepfm.presentation.main.components.MainScreen
import com.wishnewjam.dubstepfm.ui.home.HomeViewModelImpl
import com.wishnewjam.theme.DubstepTheme

@Composable
fun DubstepApp(homeViewModel: HomeViewModelImpl) {
    DubstepTheme {
        AppContent(homeViewModel = homeViewModel)
    }
}

@Composable
private fun AppContent(homeViewModel: HomeViewModelImpl) {
    Surface(color = MaterialTheme.colors.background) {
        MainScreen(viewModel = homeViewModel)
    }
}