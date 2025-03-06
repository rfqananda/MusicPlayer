package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.features.mainscreen.utils.PlayerManager
import com.example.musicplayer.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private var playerManager: PlayerManager? = null

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager?.release()
    }
}
