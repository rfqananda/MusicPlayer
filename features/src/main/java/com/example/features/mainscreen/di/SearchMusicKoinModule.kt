package com.example.features.mainscreen.di

import com.example.core.di.provideApi
import com.example.core.networking.dispatcher.CoroutinesDispatcherProvider
import com.example.core.networking.service.endpoint.SearchMusicService
import com.example.features.mainscreen.data.repository.SearchMusicRepositoryImpl
import com.example.features.mainscreen.domain.repository.SearchMusicRepository
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectSearchMusicKoinModule(): List<Module> {
    return listOf(
        dispatcherModule,
        searchMusicModelModule,
        searchMusicRepositoryModule,
        searchMusicServiceModule
    )
}

val dispatcherModule = module {
    single { CoroutinesDispatcherProvider() }
}

private val searchMusicModelModule = module {
    viewModel { SearchMusicViewModel(get(), get()) }
}

val searchMusicRepositoryModule = module {
    single<SearchMusicRepository> {
        SearchMusicRepositoryImpl(get(), get())
    }
}

val searchMusicServiceModule = module {
    single {
        provideApi<SearchMusicService>(retrofit = get())
    }
}