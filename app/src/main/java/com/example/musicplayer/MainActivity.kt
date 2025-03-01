package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.features.mainscreen.ui.composeable.MainScreenMusicPlayer
import com.example.features.mainscreen.utils.PlayerManager
import com.example.musicplayer.di.injectBaseKoinModule
import com.example.musicplayer.ui.theme.MusicPlayerTheme
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
            MusicPlayerTheme(false) {
                playerManager?.let { MainScreenMusicPlayer(playerManager = it) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager?.release()
    }
}
