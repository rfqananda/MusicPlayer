package com.example.features.mainscreen.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.networking.dispatcher.CoroutinesDispatcherProvider
import com.example.core.state.UiSafeState
import com.example.core.utils.Constants.ApiError.UNKNOWN_ERROR
import com.example.core.utils.DomainResult
import com.example.features.mainscreen.domain.repository.SearchMusicRepository
import com.example.features.mainscreen.ui.mapper.toUi
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchMusicViewModel(
    private val repository: SearchMusicRepository,
    private val dispatcher: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _searchMusic =
        MutableStateFlow<UiSafeState<SearchMusicModelUi>>(UiSafeState.Uninitialized)
    val searchMusic: StateFlow<UiSafeState<SearchMusicModelUi>> = _searchMusic.asStateFlow()

    fun searchMusic(term: String) = viewModelScope.launch(dispatcher.io) {
        _searchMusic.update { UiSafeState.Loading }
        when (val response = repository.getSearchMusic(term)) {
            is DomainResult.Success -> {
                val data = response.data.toUi()
                _searchMusic.update { UiSafeState.Success(data) }
            }

            is DomainResult.ErrorState -> {
                _searchMusic.update { UiSafeState.Error(response.message ?: UNKNOWN_ERROR) }
            }

            is DomainResult.EmptyState -> {
                _searchMusic.update { UiSafeState.Empty }
            }

            else -> {
                _searchMusic.update { UiSafeState.Error(UNKNOWN_ERROR) }
            }
        }
    }

    fun onItemSelected(selectedPosition: Int) = viewModelScope.launch(dispatcher.computation) {
        val currentState = _searchMusic.value
        if (currentState is UiSafeState.Success) {
            val currentData = currentState.data
            val updatedResults = currentData.results.mapIndexed { index, track ->
                track.copy(isSelected = index == selectedPosition)
            }

            _searchMusic.update {
                UiSafeState.Success(
                    currentData.copy(results = updatedResults)
                )
            }
        }
    }
}
