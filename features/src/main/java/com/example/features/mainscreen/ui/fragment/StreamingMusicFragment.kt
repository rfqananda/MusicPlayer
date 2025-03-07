package com.example.features.mainscreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.state.UiSafeState
import com.example.features.R
import com.example.features.databinding.StreamingMusicFragmentBinding
import com.example.features.mainscreen.ui.adapter.ListMusicAdapter
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.features.mainscreen.utils.PlaybackState
import com.example.features.mainscreen.utils.PlayerManager
import com.example.features.mainscreen.utils.formatTime
import com.example.uicomponent.extension.gone
import com.example.uicomponent.extension.visible
import com.example.uicomponent.extension.visibleIf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class StreamingMusicFragment : Fragment(), AndroidScopeComponent {

    override val scope by fragmentScope()

    private var _binding: StreamingMusicFragmentBinding? = null
    private val binding get() = _binding!!

    private val searchMusicViewModel: SearchMusicViewModel by viewModel()
    private val listMusicAdapter by lazy { ListMusicAdapter() }
    private lateinit var playerManager: PlayerManager

    private var searchKeyword = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamingMusicFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayerManager()
        setupViews()
        setupObservers()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        playerManager.release()
    }

    private fun initializePlayerManager() {
        playerManager = PlayerManager(requireContext(), lifecycleScope)
        setupPlayerController()
    }

    private fun setupViews() {
        setupRecyclerView()
        setupSearchBar()
    }

    private fun setupRecyclerView() = with(binding) {
        rvMusic.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listMusicAdapter
        }
    }

    private fun setupSearchBar() = with(binding) {
        searchBar.apply {
            onSearchActionListener = { keyword ->
                searchKeyword = keyword
                searchMusicViewModel.searchMusic(keyword)
            }
            onEndIconClickListener = {
                playerManager.reset()
                searchMusicViewModel.resetSearch()
                rvMusic.gone()
                emptyView.root.gone()
                errorView.root.gone()
                startView.root.visible()
            }
        }
    }

    private fun setupPlayerController() = with(binding) {
        playerController.apply {
            seekBar.max = 100
            setupSeekBarListener()
        }
    }

    private fun setupListeners() {
        setupAdapterClickListener()
        setupPlayerControls()
        setupErrorRetryButton()
    }

    private fun setupAdapterClickListener() {
        listMusicAdapter.setOnItemClickListener { position ->
            searchMusicViewModel.onItemSelected(position)
        }
    }

    private fun setupPlayerControls() = with(binding) {
        playerController.apply {
            btnPlayPause.setOnClickListener { playerManager.playPause() }
            btnPrevious.setOnClickListener { searchMusicViewModel.previousTrack() }
            btnNext.setOnClickListener { searchMusicViewModel.nextTrack() }
        }
    }

    private fun setupErrorRetryButton() = with(binding) {
        errorView.btnRetry.setOnClickListener {
            searchMusicViewModel.searchMusic(searchKeyword)
        }
    }

    private fun setupSeekBarListener() = with(binding) {
        binding.playerController.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) playerController.tvCurrentTime.text =
                        formatTime(progress.toLong())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    playerManager.seekTo(seekBar.progress.toLong())
                }
            }
        )
    }

    private fun setupObservers() {
        observeSearchResults()
        observePlayerState()
        observePlaybackMetadata()
        observePreviewUrls()
    }

    private fun observeSearchResults() = with(binding) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchMusicViewModel.searchMusic.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun observePlayerState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerManager.playbackState.collect { state ->
                    updatePlaybackControls(state)
                    handlePlaybackCompletion(state)
                }
            }
        }
    }

    private fun observePlaybackMetadata() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeCurrentPosition() }
                launch { observeTrackDuration() }
            }
        }
    }

    private fun observePreviewUrls() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchMusicViewModel.selectedPreviewUrl
                    .combine(searchMusicViewModel.playTrigger) { previewUrl, _ ->
                        previewUrl
                    }
                    .collect { previewUrl ->
                        previewUrl?.let {
                            playerManager.preparePlayer(it)
                        }
                    }
            }
        }
    }

    private fun observeCurrentPosition() = with(binding) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerManager.currentPosition.collect { position ->
                    playerController.apply {
                        tvCurrentTime.text = formatTime(position)
                        seekBar.progress = position.toInt()
                    }
                }
            }
        }
    }

    private fun observeTrackDuration() = with(binding) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerManager.duration.collect { duration ->
                    playerController.apply {
                        tvDuration.text = formatTime(duration)
                        seekBar.max = duration.toInt()
                    }
                }
            }
        }

    }

    private fun handleUiState(state: UiSafeState<SearchMusicModelUi>) = with(binding) {
        val isLoading = state is UiSafeState.Loading
        val isError = state is UiSafeState.Error || state is UiSafeState.ErrorConnection
        val isEmpty = state is UiSafeState.Empty
        val isSuccess = state is UiSafeState.Success

        loadingList.visibleIf(isLoading)
        errorView.root.visibleIf(isError)
        emptyView.root.visibleIf(isEmpty)
        startView.root.visibleIf(!(isLoading || isError || isEmpty || isSuccess))
        when (state) {
            is UiSafeState.Success -> handleSuccessState(state.data)
            is UiSafeState.Error -> showError(state.message)
            is UiSafeState.ErrorConnection -> showError()
            else -> {}
        }
    }

    private fun handleSuccessState(data: SearchMusicModelUi) = with(binding) {
        rvMusic.visibleIf(data.results.isNotEmpty())
        emptyView.root.visibleIf(data.results.isEmpty())
        listMusicAdapter.submitData(data.results)
    }

    private fun updatePlaybackControls(state: PlaybackState) = with(binding) {
        playerController.btnPlayPause.setImageResource(
            when (state) {
                PlaybackState.PLAYING -> R.drawable.pause
                else -> R.drawable.play
            }
        )
    }

    private fun handlePlaybackCompletion(state: PlaybackState) {
        if (state == PlaybackState.ENDED) {
            searchMusicViewModel.resetSelection()
            playerManager.reset()
        }
    }

    private fun showError(errorMessage: String = "") = with(binding) {
        errorView.tvError.text = errorMessage.ifEmpty { "Terjadi kesalahan..." }
    }
}
