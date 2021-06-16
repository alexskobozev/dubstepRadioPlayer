package com.wishnewjam.dubstepfm.legacy

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.wishnewjam.dubstepfm.ui.home.HomeScreen
import com.wishnewjam.dubstepfm.ui.home.HomeViewModelImpl
import com.wishnewjam.theme.DubstepTheme

@Composable
fun DubstepApp(navigationViewModel: NavigationViewModel,
               homeViewModel: HomeViewModelImpl) {
    DubstepTheme {
        AppContent(navigationViewModel = navigationViewModel,
                homeViewModel = homeViewModel)
    }
}

@Composable
private fun AppContent(navigationViewModel: NavigationViewModel,
                       homeViewModel: HomeViewModelImpl) {
    Surface(color = MaterialTheme.colors.background) {
        HomeScreen(navigateTo = navigationViewModel::navigateTo,
                homeViewModel = homeViewModel)
    }
}