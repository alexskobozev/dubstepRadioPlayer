package com.wishnewjam.dubstepfm.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.legacy.Screen
import com.wishnewjam.dubstepfm.ui.state.UiState
import com.wishnewjam.dubstepfm.ui.ThemedPreview

@Composable
fun HomeScreen(navigateTo: (Screen) -> Unit,
               homeViewModel: HomeViewModel) {
    Scaffold(topBar = {
        val title = stringResource(id = R.string.app_name)


        TopAppBar(title = { Text(text = title) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.ic_settings),
                                contentDescription = stringResource(id = R.string.action_settings))
                    }
                })
    },
            content = {
                StatusView(homeViewModel)
                MainView(homeViewModel)
            })
}

@Composable
private fun MainView(homeViewModel: HomeViewModel) {
    Column(modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
            verticalArrangement = Arrangement.Bottom) {
        Box(modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()) {
            Image(modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(R.drawable.img_logo),
                    contentDescription = stringResource(id = R.string.logo_content_desc))
        }
        IconButton(onClick = homeViewModel::toggleButton,
                Modifier
                        .height(140.dp)
                        .fillMaxWidth()) {
            val state: UiState? by homeViewModel.playButtonState.observeAsState()
            val playButtonResource = if (state == UiState.Play) {
                R.drawable.ic_play
            }
            else {
                R.drawable.ic_stop
            }
            Image(modifier = Modifier
                    .fillMaxHeight()
                    .size(100.dp),
                    painter = painterResource(playButtonResource),
                    contentDescription = stringResource(R.string.play))
        }
    }
}

@Composable
private fun StatusView(homeViewModel: HomeViewModel) {
    val statusText by homeViewModel.statusText.observeAsState()
    if (statusText != null) {
        val padding = 8.dp
        Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(padding)) {
            val iconRes by homeViewModel.statusIcon.observeAsState()
            StatusIcon(iconRes)
            Spacer(modifier = Modifier.size(padding))
            Column {
                val nowPlaying: String? by homeViewModel.nowPlaying.observeAsState()
                val fontFamily = FontFamily(Font(R.font.montserrat_regular,
                        FontWeight.Normal),
                        Font(R.font.montserrat_semibold,
                                FontWeight.Bold))
                Text(statusText ?: "",
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily)
                if (nowPlaying != null) {
                    Text(nowPlaying ?: "",
                            fontFamily = fontFamily)
                }
            }
        }
    }
}

@Composable
private fun StatusIcon(iconRes: Int?) {
    val modifier = Modifier
            .size(32.dp)
            .padding(2.dp)
    if (iconRes == null) {
        CircularProgressIndicator(modifier = modifier)
    }
    else {
        Image(modifier = modifier,
                painter = painterResource(iconRes),
                contentDescription = stringResource(R.string.play))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    ThemedPreview(darkTheme = true) {
        HomeScreen(navigateTo = { },
                homeViewModel = HomeViewModelPreview())
    }
}