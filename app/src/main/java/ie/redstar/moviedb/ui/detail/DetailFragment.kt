package ie.redstar.moviedb.ui.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import dagger.hilt.android.AndroidEntryPoint
import ie.redstar.moviedb.R
import ie.redstar.moviedb.databinding.DetailFragmentBinding

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DetailFragmentBinding.inflate(inflater, container, false)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetailViewModel.State.Loaded -> renderLoadedState(binding, state)
            }
        }

        return binding.root
    }

    private fun renderLoadedState(
        binding: DetailFragmentBinding,
        state: DetailViewModel.State.Loaded
    ) {
        binding.movieTitle.text = state.movie.title
        binding.releaseDate.text = state.movie.releaseDate
        binding.progressBar.progress = state.movie.voteAverage
        binding.percent.text = getString(R.string.popularity_percent, state.movie.voteAverage)
        binding.posterImage.load(state.movie.posterUrl) {
            crossfade(true)
            error(ColorDrawable(Color.LTGRAY))
            transformations(RoundedCornersTransformation(25f))
        }
        binding.backdropImage.load(state.movie.backdropUrl) {
            crossfade(true)
            error(ColorDrawable(Color.LTGRAY))
        }
        binding.overview.text = state.movie.overview
    }
}