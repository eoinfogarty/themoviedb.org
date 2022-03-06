package ie.redstar.moviedb.data.model

data class MovieListResponseDto(
    val page: Int,
    val results: List<MovieListDto>
)
