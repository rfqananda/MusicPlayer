package com.example.features.mainscreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.state.UiSafeState
import com.example.features.databinding.StreamingMusicFragmentBinding
import com.example.features.mainscreen.ui.adapter.ListMusicAdapter
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.features.mainscreen.utils.PlayerManager
import com.example.uicomponent.extension.visibleIf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class StreamingMusicFragment : Fragment(), AndroidScopeComponent {

    override val scope by fragmentScope()

    private var _binding: StreamingMusicFragmentBinding? = null
    private val binding get() = _binding

    private val searchMusicViewModel: SearchMusicViewModel by viewModel()

    private val listMusicAdapter by lazy {
        ListMusicAdapter()
    }

    private var playerManager: PlayerManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StreamingMusicFragmentBinding.inflate(inflater, container, false)
        val view = binding?.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayerManager()
        fetchData()
        initLiveData()
        initView()
        initListener()
    }

    private fun fetchData() {
        searchMusicViewModel.searchMusic("Ed Sheeran Thinking Out Loud")
    }

    private fun initPlayerManager(){
        playerManager = PlayerManager(requireContext(), lifecycleScope)
    }

    private fun initView() {
        initAdapter()
    }

    private fun initListener(){
        listMusicAdapter.setOnItemClickListener { position ->
            searchMusicViewModel.onItemSelected(position)
        }
        binding?.errorView?.btnRetry?.setOnClickListener {
            searchMusicViewModel.searchMusic("tompi")
        }
    }

    private fun initAdapter() {
        binding?.rvMusic?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listMusicAdapter
        }
    }


    private fun initLiveData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchMusicViewModel.searchMusic.collect { state ->
                    binding?.loadingList?.visibleIf(state is UiSafeState.Loading)
                    binding?.errorView?.root?.visibleIf(
                        state is UiSafeState.Error || state is UiSafeState.ErrorConnection
                    )
                    binding?.emptyView?.root?.visibleIf(state is UiSafeState.Empty)
                    handleDataState(state)
                }
            }


        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchMusicViewModel.selectedPreviewUrl
                    .combine(searchMusicViewModel.playTrigger) { previewUrl, _ ->
                        previewUrl
                    }
                    .collect { previewUrl ->
                        previewUrl?.let {
                            playerManager?.preparePlayer(it)
                        }
                    }
            }
        }
    }

    private fun handleDataState(
        state: UiSafeState<SearchMusicModelUi>
    ) {
        when (state) {
            is UiSafeState.Success -> {
                val data = state.data.results
                binding?.rvMusic?.visibleIf(data.isNotEmpty())
                binding?.emptyView?.root?.visibleIf(data.isEmpty())
                data.takeIf { it.isNotEmpty() }?.let { listMusicAdapter.submitData(it) }
            }

            is UiSafeState.Error -> {
            }

            is UiSafeState.Empty -> {
            }

            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        binding?.rvMusic?.adapter = null
        playerManager?.release()
    }
}
