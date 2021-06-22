package com.wishnewjam.dubstepfm.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wishnewjam.dubstepfm.R
import com.wishnewjam.dubstepfm.ui.ThemedPreview

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    Scaffold(topBar = {
        val title = stringResource(id = R.string.app_name)


        TopAppBar(title = { Text(text = title) },
            actions = {
                DropdownDemo()
//                IconButton(onClick = {}) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_settings),
//                        contentDescription = stringResource(id = R.string.action_settings)
//                    )
//                }
            })
    },
        content = {
            StatusView(homeViewModel)
            MainView(homeViewModel)
        })
}

@Composable
fun DropdownDemo() {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("24 kbps", "64 kbps", "128 kbps", "256 kbps")
    var selectedIndex by remember { mutableStateOf(0) }
    Box(modifier = Modifier
        .wrapContentSize(Alignment.TopStart)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(48.dp)
                .padding(8.dp)
                .clickable(onClick = { expanded = true })) {
            Image(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = stringResource(R.string.choose_bitrate)
            )
            val fontFamily = FontFamily(
                Font(
                    R.font.montserrat_regular,
                    FontWeight.Normal
                ),
                Font(
                    R.font.montserrat_semibold,
                    FontWeight.Bold
                )
            )
            Text(items[selectedIndex],
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontFamily = fontFamily)

        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.Gray)
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                }) {
                    Text(text = s)
                }
            }
        }
    }
}

@Composable
private fun MainView(homeViewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(R.drawable.img_logo),
                contentDescription = stringResource(id = R.string.logo_content_desc)
            )
        }
        IconButton(
            onClick = homeViewModel::toggleButton,
            Modifier
                .height(140.dp)
                .fillMaxWidth()
        ) {
            val playButtonRes by homeViewModel.playButtonRes.observeAsState(homeViewModel.initialPlayButtonState)
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(100.dp),
                painter = painterResource(playButtonRes),
                contentDescription = stringResource(R.string.play)
            )
        }
    }
}

@Composable
private fun StatusView(homeViewModel: HomeViewModel) {
    val statusText by homeViewModel.statusText.observeAsState()
    if (statusText == null) return
    val padding = 8.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(padding)
    ) {
        val iconRes by homeViewModel.statusIcon.observeAsState()
        StatusIcon(iconRes)
        Spacer(modifier = Modifier.size(padding))
        Column {
            val nowPlaying: String? by homeViewModel.nowPlaying.observeAsState()
            val fontFamily = FontFamily(
                Font(
                    R.font.montserrat_regular,
                    FontWeight.Normal
                ),
                Font(
                    R.font.montserrat_semibold,
                    FontWeight.Bold
                )
            )
            Text(
                statusText ?: "",
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            if (nowPlaying != null) {
                Text(
                    nowPlaying ?: "",
                    fontFamily = fontFamily
                )
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
    } else {
        Image(
            modifier = modifier,
            painter = painterResource(iconRes),
            contentDescription = stringResource(R.string.play)
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    ThemedPreview(darkTheme = true) {
        HomeScreen(homeViewModel = HomeViewModelPreview())
    }
}