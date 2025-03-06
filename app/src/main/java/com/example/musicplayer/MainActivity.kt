package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.features.mainscreen.utils.PlayerManager
import com.example.musicplayer.databinding.MainActivityBinding
import com.example.musicplayer.di.injectBaseKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.scope.Scope

class MainActivity : AppCompatActivity(), AndroidScopeComponent {


    override val scope: Scope by activityScope()

    private var playerManager: PlayerManager? = null

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin{
            androidLogger()
            androidContext(this@MainActivity)
            injectBaseKoinModule()
        }

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager?.release()
    }
}
