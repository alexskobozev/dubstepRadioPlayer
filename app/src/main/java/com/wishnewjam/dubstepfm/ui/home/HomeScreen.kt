package com.wishnewjam.dubstepfm.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.Screen
import com.wishnewjam.dubstepfm.UiState
import com.wishnewjam.dubstepfm.data.StreamsRepository
import com.wishnewjam.dubstepfm.ui.ThemedPreview
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navigateTo: (Screen) -> Unit,
               streamsRepository: StreamsRepository,
               scaffoldState: ScaffoldState = rememberScaffoldState(),
               homeViewModel: HomeViewModel) {

    val coroutineScope = rememberCoroutineScope()

    HomeScreen(navigateTo = navigateTo,
            scaffoldState = scaffoldState,
            homeViewModel = homeViewModel)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(navigateTo: (Screen) -> Unit,
               scaffoldState: ScaffoldState,
               homeViewModel: HomeViewModel) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(scaffoldState = scaffoldState,
            topBar = {
                val title = stringResource(id = R.string.app_name)
                TopAppBar(title = { Text(text = title) },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                                Icon(painter = painterResource(android.R.drawable.ic_delete),
                                        contentDescription = stringResource(android.R.string.cancel))
                            }
                        })
            },
            content = { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                val state: UiState? by homeViewModel.playButtonState.observeAsState()
                val playButtonResource = if (state == UiState.Play) {
                    R.drawable.ic_stop
                }
                else {
                    R.drawable.ic_play
                }
                Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                        verticalArrangement = Arrangement.Bottom) {
                    Box(modifier = modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {
                        Image(modifier = Modifier.align(Alignment.Center),
                                painter = painterResource(R.drawable.img_logo),
                                contentDescription = stringResource(id = R.string.logo_content_desc))
                    }
                    IconButton(onClick = homeViewModel::toggleButton,
                            modifier
                                    .height(140.dp)
                                    .fillMaxWidth()) {

                        Image(modifier = Modifier
                                .fillMaxHeight()
                                .size(100.dp),
                                painter = painterResource(playButtonResource),
                                contentDescription = stringResource(R.string.play))
                    }
                }
            })
}

@Preview
@Composable
fun HomeScreenPreview() {
    ThemedPreview(darkTheme = true) {
        val scaffoldState =
                rememberScaffoldState(drawerState = rememberDrawerState(DrawerValue.Open))

        HomeScreen(scaffoldState = scaffoldState,
                navigateTo = { },
                homeViewModel = HomeViewModel())
    }
}