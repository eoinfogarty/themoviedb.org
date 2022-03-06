package ie.redstar.moviedb.domain.repository

import ie.redstar.moviedb.BuildConfig
import ie.redstar.moviedb.data.remote.MovieDbApi
import ie.redstar.moviedb.domain.model.MovieListModel
import ie.redstar.moviedb.domain.model.MovieListResultModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface PopularMovieRepository {

    val movieResult: StateFlow<MovieListResultModel>

    suspend fun loadMorePopularMovies()
    suspend fun refreshPopularMovies()
}

class PopularMovieDataSource @Inject constructor(
    private val movieDbApi: MovieDbApi
) : PopularMovieRepository {

    companion object {
        private const val baseImageUrl = "https://image.tmdb.org/t/p/w500"
        private val dateFormatter = DateTimeFormatter.ofPattern("(yyyy)")
        private val emptyResult = MovieListResultModel(isLoading = true)
    }

    private val _movieResult = MutableStateFlow(emptyResult)
    override val movieResult: StateFlow<MovieListResultModel> = _movieResult.asStateFlow()

    override suspend fun loadMorePopularMovies() {
        getPopularMovies(_movieResult.value.page + 1)
    }

    override suspend fun refreshPopularMovies() {
        getPopularMovies(1)
    }

    private suspend fun getPopularMovies(page: Int) {
        val movieList = _movieResult.value

        _movieResult.emit(movieList.copy(isLoading = true))

        try {
            val response = movieDbApi.getPopularMovies(
                apiKey = BuildConfig.API_KEY,
                page = page
            )

            val results = response.results.map { movie ->
                MovieListModel(
                    backdropUrl = baseImageUrl + movie.backdropPath,
                    id = movie.id,
                    overview = movie.overview,
                    voteAverage = (movie.voteAverage * 10).toInt(),
                    posterUrl = baseImageUrl + movie.posterPath,
                    releaseDate = movie.releaseDate?.let(dateFormatter::format) ?: "N/A",
                    title = movie.title
                )
            }

            val updateResults = if (page == 1) {
                results.sortedByDescending { it.voteAverage }
            } else {
                movieList.results + results.sortedByDescending { it.voteAverage }
            }

            val result = MovieListResultModel(page = response.page, results = updateResults)
            _movieResult.emit(result)
        } catch (error: Exception) {
            val result = MovieListResultModel(
                error = error,
                page = movieList.page,
                results = movieList.results
            )
            _movieResult.emit(result)
        }
    }
}