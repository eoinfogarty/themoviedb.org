package ie.redstar.moviedb.ui.main

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import ie.redstar.moviedb.R
import ie.redstar.moviedb.domain.model.MovieListModel
import ie.redstar.moviedb.ui.main.item.LoadingMoreItem
import ie.redstar.moviedb.ui.main.item.MovieListItem
import ie.redstar.moviedb.ui.main.item.TitleListItem

class MainListAdapter(
    private val onClickMovie: (MovieListModel) -> Unit
) : GroupAdapter<GroupieViewHolder>() {

    private val loadingMoreItem = LoadingMoreItem()

    fun updateMovieList(movies: List<MovieListModel>) {
        val items = mutableListOf<BindableItem<*>>()
        items.add(TitleListItem(R.string.popular_movies))
        movies.forEach { movie ->
            items.add(MovieListItem(movie, onClickMovie))
        }

        update(items)
    }

    fun showLoadingMore(show: Boolean) {
        val adapterPosition = getAdapterPosition(loadingMoreItem)
        if (show && adapterPosition == -1) {
            add(groupCount, loadingMoreItem)
        } else if (!show && adapterPosition != -1) {
            remove(loadingMoreItem)
        }
    }
}
