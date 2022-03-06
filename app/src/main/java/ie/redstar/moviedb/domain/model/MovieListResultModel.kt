package ie.redstar.moviedb.domain.model

data class MovieListResultModel(
    val error: Exception? = null,
    val isLoading: Boolean = false,
    val page: Int = 1,
    val results: List<MovieListModel> = emptyList()
)
