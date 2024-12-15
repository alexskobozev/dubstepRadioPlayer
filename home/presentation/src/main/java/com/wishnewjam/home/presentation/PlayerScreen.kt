package com.wishnewjam.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wishnewjam.commons.design.R
import com.wishnewjam.commons.design.buttonBackground
import com.wishnewjam.commons.design.buttonTextCommon
import com.wishnewjam.home.domain.PlayerViewModel

@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val uiState by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlaybackStatusBar(uiState)
            }
            MainLogo(modifier = Modifier.weight(weight = 1f))
            PlayButton(uiState) {
                viewModel.clickPlayButton()
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (uiState.isLoading) {
            LoadingPop()
        }
    }
}

@Composable
private fun LoadingPop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()

            Text(
                text = "Loading",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PlayButton(
    uiState: PlayerViewModel.UiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Button(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    colors = ButtonDefaults.buttonColors(
        containerColor = buttonBackground,
        contentColor = buttonTextCommon
    )
) {
    if (uiState.isPlaying) {
        Image(
            painter = painterResource(id = R.drawable.ic_stop),
            contentDescription = "Stop",
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_play),
            contentDescription = "Play",
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
        )
    }
}

@Composable
private fun MainLogo(modifier: Modifier = Modifier) = Box(
    modifier = modifier
        .fillMaxWidth(),
    contentAlignment = Alignment.Center
) {
    Image(
        painter = painterResource(id = R.drawable.img_logo),
        contentDescription = "Logo",
        modifier = Modifier.padding(36.dp)
    )
}

@Composable
private fun PlaybackStatusBar(uiState: PlayerViewModel.UiState, modifier: Modifier = Modifier) {
    if (uiState.isLoading) {
        CircularProgressIndicator(
            modifier = modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
    }

    Image(
        painter = painterResource(id = R.drawable.ic_stop),
        contentDescription = "Stop",
        modifier = modifier
            .size(24.dp)
            .padding(end = 8.dp)
    )

    Text(
        text = uiState.nowPlaying,
        fontSize = 18.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}
