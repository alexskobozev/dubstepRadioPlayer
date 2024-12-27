package com.wishnewjam.home.presentation

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.wishnewjam.commons.design.Bitsumishi
import com.wishnewjam.commons.design.R
import com.wishnewjam.home.domain.PlayerViewModel

@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val uiState by viewModel.state.collectAsState()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2C3137),
                        Color(0xFF17191D)  // Second color
                    ),
                    start = Offset(-200f, 200f),
                    end = Offset(1000f, 200f)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "DUBSTEP.FM",
                fontFamily = Bitsumishi,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            )
            MainLogo(modifier = Modifier.weight(weight = 1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.year,
                    fontFamily = Bitsumishi,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.nowPlaying,
                    fontFamily = Bitsumishi,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color(0xFFE34715)
                    ),
                    lineHeight = 20.sp,
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            PlayButton(uiState) {
                viewModel.clickPlayButton()
            }
            Spacer(modifier = Modifier.height(48.dp))
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
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(86.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Blue,
                spotColor = Color(0xFFDB4C0B)
            )
            .background(
                shape = CircleShape,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF9465), Color(0xFFAF1905)),
                ),
            )
            .clickable(
                onClick = onClick,
            ),
    ) {
        Box(
            modifier = modifier
                .size(72.dp)
                .padding(0.dp),
        ) {
            if (uiState.isPlaying) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_pause),
                    contentDescription = "Pause",
                    modifier = modifier
                        .size(72.dp)
                        .background(
                            shape = CircleShape,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFEC540E), Color(0xFFD6361F)),
                            )
                        )
                        .padding(8.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_play),
                    contentDescription = "Play",
                    modifier = modifier
                        .size(72.dp)
                        .background(
                            shape = CircleShape,
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFEC540E), Color(0xFFD6361F)),
                            )
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MainLogo(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current // todo to heavy
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(36.dp)
                .size(screenWidth * 0.5f)
        )
    }
}