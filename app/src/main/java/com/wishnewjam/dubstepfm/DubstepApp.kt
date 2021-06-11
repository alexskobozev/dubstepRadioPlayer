package com.wishnewjam.dubstepfm

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.wishnewjam.dubstepfm.data.AppContainer
import com.wishnewjam.dubstepfm.data.StreamsRepository
import com.wishnewjam.dubstepfm.ui.home.HomeScreen
import com.wishnewjam.dubstepfm.ui.home.HomeViewModel
import com.wishnewjam.theme.DubstepTheme

@Composable
fun DubstepApp(appContainer: AppContainer,
               navigationViewModel: NavigationViewModel,
               homeViewModel: HomeViewModel) {
    DubstepTheme {
        AppContent(navigationViewModel = navigationViewModel,
                homeViewModel = homeViewModel,
                streamsRepository = appContainer.streamRepository)
    }
}

@Composable
private fun AppContent(navigationViewModel: NavigationViewModel,
                       homeViewModel: HomeViewModel,
                       streamsRepository: StreamsRepository) {
    Surface(color = MaterialTheme.colors.background) {
        HomeScreen(navigateTo = navigationViewModel::navigateTo,
                homeViewModel = homeViewModel,
                streamsRepository = streamsRepository)
    }
}