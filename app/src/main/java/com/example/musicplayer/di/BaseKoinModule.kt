package com.example.musicplayer.di

import com.example.core.di.networkModule
import com.example.features.mainscreen.di.injectSearchMusicKoinModule
import org.koin.core.context.loadKoinModules


fun injectBaseKoinModule() {
    loadKoinModules(
        injectSearchMusicKoinModule() + networkModule()
    )
}