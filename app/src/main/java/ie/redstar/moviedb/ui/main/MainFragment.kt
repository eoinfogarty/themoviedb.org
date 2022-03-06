package ie.redstar.moviedb.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ie.redstar.moviedb.R
import ie.redstar.moviedb.databinding.MainFragmentBinding
import ie.redstar.moviedb.ui.util.EndlessScrollListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View {
        val binding = MainFragmentBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(activity)
        val scrollListener = object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore() {
                viewModel.loadMoreMovies()
            }
        }
        val movieAdapter = MainListAdapter(
            onClickMovie = { movie ->
                val directions = MainFragmentDirections.actionMainFragmentToDetailFragment(movie)
                findNavController().navigate(directions)
            }
        )

        binding.swipeRefresh.setColorSchemeResources(R.color.green)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshMovies()
            scrollListener.resetState()
        }

        binding.loadedState.movieList.apply {
            layoutManager = linearLayoutManager
            adapter = movieAdapter
            addOnScrollListener(scrollListener)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                MainViewModel.State.Empty -> renderEmptyState(binding)
                is MainViewModel.State.Loaded -> renderLoadedState(binding, state, movieAdapter)
                MainViewModel.State.Loading -> renderLoadingState(binding)
                MainViewModel.State.LoadingMore -> renderLoadingMoreState(movieAdapter)
                MainViewModel.State.Error -> renderErrorState(binding)
            }
        }

        viewModel.events.onEach { event ->
            when (event) {
                MainViewModel.Event.ShowErrorToast ->
                    showErrorToast(binding, movieAdapter, scrollListener)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }

    private fun showErrorToast(
        binding: MainFragmentBinding,
        movieAdapter: MainListAdapter,
        scrollListener: EndlessScrollListener
    ) {
        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        binding.swipeRefresh.isRefreshing = false
        movieAdapter.showLoadingMore(false)
        scrollListener.resetState()
    }

    private fun renderErrorState(binding: MainFragmentBinding) {
        binding.viewFlipper.displayedChild = 3
        binding.swipeRefresh.isRefreshing = false
    }

    private fun renderLoadingMoreState(movieAdapter: MainListAdapter) {
        movieAdapter.showLoadingMore(true)
    }

    private fun renderEmptyState(binding: MainFragmentBinding) {
        binding.viewFlipper.displayedChild = 2
        binding.swipeRefresh.isRefreshing = false
    }

    private fun renderLoadedState(
        binding: MainFragmentBinding,
        state: MainViewModel.State.Loaded,
        movieAdapter: MainListAdapter
    ) {
        binding.viewFlipper.displayedChild = 1
        binding.swipeRefresh.isRefreshing = false
        movieAdapter.updateMovieList(state.movieResult.results)
        movieAdapter.showLoadingMore(false)
    }

    private fun renderLoadingState(binding: MainFragmentBinding) {
        binding.viewFlipper.displayedChild = 0
    }
}