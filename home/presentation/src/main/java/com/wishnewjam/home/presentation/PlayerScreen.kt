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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wishnewjam.commons.design.buttonBackground
import com.wishnewjam.commons.design.buttonTextCommon

@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val nowPlayingText by viewModel.nowPlayingText.observeAsState(initial = "Nothing to play")
    val progressBarVisible by viewModel.progressBarVisible.observeAsState(
        initial = false
    )
    val loadingVisible by viewModel.loadingVisible.observeAsState(initial = false)

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
                if (progressBarVisible) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                }

                Image(
                    painter = painterResource(id = com.wishnewjam.commons.design.R.drawable.ic_stop),
                    contentDescription = "Stop",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )

                Text(
                    text = nowPlayingText,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.wishnewjam.commons.design.R.drawable.img_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(36.dp)
                )
            }

            Button(
                onClick = { viewModel.play() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackground,
                    contentColor = buttonTextCommon
                )
            ) {
                Text(
                    text = "Play",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(23.dp))

            Button(
                onClick = { viewModel.stop() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackground,
                    contentColor = buttonTextCommon
                )
            ) {
                Text(
                    text = "Stop",
                    fontSize = 24.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (loadingVisible) {
            Box(
                modifier = Modifier
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
    }
}
