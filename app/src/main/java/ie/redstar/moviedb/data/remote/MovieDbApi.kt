package ie.redstar.moviedb.data.remote

import ie.redstar.moviedb.data.model.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieDbApi {

    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponseDto
}