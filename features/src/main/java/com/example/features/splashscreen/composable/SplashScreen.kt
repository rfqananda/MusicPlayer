package com.example.features.splashscreen.composable

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.features.splashscreen.composable.SplashScreenConstants.DELAY
import com.example.features.splashscreen.composable.SplashScreenConstants.DURATION_MILLIS
import com.example.features.splashscreen.composable.SplashScreenConstants.IMAGE_DESC
import com.example.features.splashscreen.composable.SplashScreenConstants.INITIAL_VALUE
import com.example.features.splashscreen.composable.SplashScreenConstants.MAIN_SCREEN
import com.example.features.splashscreen.composable.SplashScreenConstants.SIZE
import com.example.features.splashscreen.composable.SplashScreenConstants.TARGET_VALUE
import com.example.features.splashscreen.composable.SplashScreenConstants.TENSION
import com.example.uicomponent.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    val context = LocalContext.current
    val containerColor = remember(context) {
        Color(ContextCompat.getColor(context, R.color.bluePrimary))
    }

    val scale = remember {
        Animatable(INITIAL_VALUE)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = TARGET_VALUE,
            animationSpec = tween(
                durationMillis = DURATION_MILLIS,
                easing = {
                    OvershootInterpolator(TENSION).getInterpolation(it)
                }
            )
        )
        delay(DELAY)
        navController.navigate(MAIN_SCREEN)
    }
    Box (
        modifier = Modifier.fillMaxSize().background(containerColor),
        contentAlignment = Alignment.Center){
        Image(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = IMAGE_DESC,
            modifier = Modifier.scale(scale.value).size(SIZE.dp)
        )
    }

}


private object SplashScreenConstants {
    const val SIZE = 1000
    const val INITIAL_VALUE = 0f
    const val TARGET_VALUE = 0.3f
    const val DURATION_MILLIS = 500
    const val DELAY = 2500L
    const val TENSION = 2f
    const val IMAGE_DESC = "splashScreenImage"
    const val MAIN_SCREEN = "main_screen"
}
