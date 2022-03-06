package ie.redstar.moviedb.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieListModel(
    val backdropUrl: String,
    val id: Long,
    val overview: String,
    val voteAverage: Int,
    val posterUrl: String,
    val releaseDate: String,
    val title: String
) : Parcelable
