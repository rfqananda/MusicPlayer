package com.example.features.mainscreen.ui.composeable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.features.mainscreen.ui.composeable.ControllerConstants.HALF
import com.example.features.mainscreen.ui.composeable.ControllerConstants.NEXT
import com.example.features.mainscreen.ui.composeable.ControllerConstants.PLAY_PAUSE
import com.example.features.mainscreen.ui.composeable.ControllerConstants.PREVIOUS
import com.example.features.mainscreen.ui.composeable.ControllerConstants.START_POSITION
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.features.mainscreen.utils.PlaybackState
import com.example.features.mainscreen.utils.PlayerManager
import com.example.features.mainscreen.utils.formatTime
import com.example.uicomponent.R as component
import com.example.features.R as features

@Composable
fun PlayerController(
    playerManager: PlayerManager,
    viewModel: SearchMusicViewModel
) {
    val context = LocalContext.current
    val color = Color(ContextCompat.getColor(context, component.color.bottomBarColor))
    val colorTextSlider = Color(ContextCompat.getColor(context, component.color.bluePrimary))
    val cornerRadius = 16.dp
    val elevation = 1.dp

    val playbackState by playerManager.playbackState.collectAsState()
    val currentPosition by playerManager.currentPosition.collectAsState()
    val duration by playerManager.duration.collectAsState()
    var sliderPosition by remember { mutableFloatStateOf(START_POSITION) }
    var isSliding by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(color)
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
            )
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = if (isSliding) sliderPosition
                else currentPosition.coerceIn(0, duration).toFloat(),
                onValueChange = { newValue ->
                    sliderPosition = newValue
                    isSliding = true
                },
                onValueChangeFinished = {
                    playerManager.seekTo(sliderPosition.toLong())
                    isSliding = false
                },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray.copy(alpha = HALF)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = colorTextSlider,
                    fontSize = 12.sp
                )
                Text(
                    text = formatTime(duration),
                    color = colorTextSlider,
                    fontSize = 12.sp
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(modifier = Modifier.size(30.dp), onClick = {
                viewModel.previousTrack()
            }) {
                Icon(
                    painter = painterResource(id = features.drawable.back),
                    contentDescription = PREVIOUS,
                    tint = Color.Unspecified
                )
            }

            IconButton(modifier = Modifier.size(60.dp), onClick = {
                playerManager.playPause()
                if (playerManager.hasEnded()) {
                    playerManager.restartPlayer()
                }
            }) {
                Icon(
                    painter = if (playbackState == PlaybackState.PLAYING) {
                        painterResource(id = features.drawable.pause)
                    } else {
                        painterResource(id = features.drawable.play)
                    },
                    contentDescription = PLAY_PAUSE,
                    tint = Color.Unspecified
                )
            }

            IconButton(modifier = Modifier.size(30.dp), onClick = {
                viewModel.nextTrack()
            }) {
                Icon(
                    painter = painterResource(id = features.drawable.skip),
                    contentDescription = NEXT,
                    tint = Color.Unspecified
                )
            }
        }
    }
}

private object ControllerConstants {
    const val HALF = 0.5f
    const val START_POSITION = 0f
    const val NEXT = "Next"
    const val PLAY_PAUSE = "Play/Pause"
    const val PREVIOUS = "Previous"
}
