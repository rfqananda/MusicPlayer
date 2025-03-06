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
    private val dispatcher: CoroutinesDispatcherProvider,
) : ViewModel() {

    private val _searchMusic =
        MutableStateFlow<UiSafeState<SearchMusicModelUi>>(UiSafeState.Uninitialized)
    val searchMusic: StateFlow<UiSafeState<SearchMusicModelUi>> = _searchMusic.asStateFlow()

    private val _selectedPreviewUrl = MutableStateFlow<String?>(null)
    val selectedPreviewUrl: StateFlow<String?> = _selectedPreviewUrl.asStateFlow()

    private val _playTrigger = MutableStateFlow(0)
    val playTrigger: StateFlow<Int> = _playTrigger.asStateFlow()

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

            val newData = currentData.copy(results = updatedResults)
            _searchMusic.update { UiSafeState.Success(newData) }

            val selectedTrack = newData.results.getOrNull(selectedPosition)
            _selectedPreviewUrl.value = selectedTrack?.previewUrl
        }
        _playTrigger.value++
    }

    fun nextTrack() = viewModelScope.launch(dispatcher.computation) {
        val currentState = _searchMusic.value
        if (currentState !is UiSafeState.Success) return@launch
        val tracks = currentState.data.results
        if (tracks.isEmpty()) return@launch

        val currentIndex = tracks.indexOfFirst { it.isSelected }

        if (currentIndex == -1) return@launch

        val newIndex = (currentIndex + 1) % tracks.size
        onItemSelected(newIndex)
    }

    fun previousTrack() = viewModelScope.launch(dispatcher.computation) {
        val currentState = _searchMusic.value
        if (currentState !is UiSafeState.Success) return@launch
        val tracks = currentState.data.results
        if (tracks.isEmpty()) return@launch

        val currentIndex = tracks.indexOfFirst { it.isSelected }

        if (currentIndex == -1) return@launch

        val newIndex = (currentIndex - 1 + tracks.size) % tracks.size
        onItemSelected(newIndex)
    }

    fun setListEmpty(){
        val emptyData = SearchMusicModelUi()
        _searchMusic.update { UiSafeState.Success(emptyData) }
    }

    fun resetSelection() = viewModelScope.launch(dispatcher.computation) {
        val currentState = _searchMusic.value
        if (currentState is UiSafeState.Success) {
            val currentData = currentState.data
            val updatedResults = currentData.results.map { track ->
                track.copy(isSelected = false)
            }

            val newData = currentData.copy(
                results = updatedResults
            )
            _searchMusic.update { UiSafeState.Success(newData) }
            _selectedPreviewUrl.value = null
        }
    }
}
