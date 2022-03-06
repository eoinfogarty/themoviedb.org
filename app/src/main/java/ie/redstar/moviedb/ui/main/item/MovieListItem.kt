package ie.redstar.moviedb.ui.main.item

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import coil.load
import coil.transform.RoundedCornersTransformation
import com.xwray.groupie.viewbinding.BindableItem
import ie.redstar.moviedb.R
import ie.redstar.moviedb.databinding.MainListItemMovieBinding
import ie.redstar.moviedb.domain.model.MovieListModel

class MovieListItem(
    private val movie: MovieListModel,
    private val onClickMovie: (MovieListModel) -> Unit
) : BindableItem<MainListItemMovieBinding>() {

    override fun getLayout() = R.layout.main_list_item_movie

    override fun initializeViewBinding(view: View) = MainListItemMovieBinding.bind(view)

    override fun bind(viewBinding: MainListItemMovieBinding, position: Int) {
        val context = viewBinding.root.context

        viewBinding.apply {
            posterImage.load(movie.posterUrl) {
                crossfade(true)
                error(ColorDrawable(Color.LTGRAY))
                transformations(RoundedCornersTransformation(25f))
            }
            movieTitle.text = movie.title
            releaseDate.text = movie.releaseDate
            progressBar.progress = movie.voteAverage
            percent.text = context.getString(R.string.popularity_percent, movie.voteAverage)

            root.setOnClickListener {
                onClickMovie(movie)
            }
        }
    }

    override fun getId() = movie.id

    override fun hashCode() = movie.hashCode()

    override fun equals(other: Any?): Boolean {
        return (other as? MovieListItem)?.movie == movie
    }
}