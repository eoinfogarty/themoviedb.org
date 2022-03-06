package ie.redstar.moviedb.data.model

import java.time.LocalDate

data class MovieListDto(
    val backdropPath: String,
    val id: Long,
    val overview: String,
    val voteAverage: Float,
    val posterPath: String,
    val releaseDate: LocalDate?,
    val title: String
)
