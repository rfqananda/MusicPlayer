package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.features.mainscreen.ui.composeable.MainScreenMusicPlayer
import com.example.features.mainscreen.utils.PlayerManager
import com.example.features.splashscreen.composable.SplashScreen
import com.example.musicplayer.di.injectBaseKoinModule
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.utils.Constants.MAIN_SCREEN
import com.example.musicplayer.utils.Constants.SPLASH_SCREEN
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    private var playerManager: PlayerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            injectBaseKoinModule()
        }
        enableEdgeToEdge()

        playerManager = PlayerManager(this, lifecycleScope)
        setContent {
            Navigation()
        }
    }

    @Composable
    fun Navigation(){
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = SPLASH_SCREEN){
            composable(SPLASH_SCREEN){
                SplashScreen(navController = navController)
            }
            composable(MAIN_SCREEN){
                MusicPlayerTheme(false) {
                    playerManager?.let { MainScreenMusicPlayer(playerManager = it) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager?.release()
    }
}
