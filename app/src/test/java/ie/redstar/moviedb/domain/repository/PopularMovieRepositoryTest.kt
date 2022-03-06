package ie.redstar.moviedb.domain.repository

import ie.redstar.moviedb.data.model.MovieListDto
import ie.redstar.moviedb.data.model.MovieListResponseDto
import ie.redstar.moviedb.data.remote.MovieDbApi
import ie.redstar.moviedb.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PopularMovieRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Successful call is not loading and has no error`() = runTest {
        val dataSource = PopularMovieDataSource(object : MovieDbApi {
            override suspend fun getPopularMovies(apiKey: String, page: Int): MovieListResponseDto {
                return MovieListResponseDto(page, emptyList())
            }
        })

        dataSource.refreshPopularMovies()

        assertEquals(false, dataSource.movieResult.value.isLoading)
        assertEquals(null, dataSource.movieResult.value.error)
    }

    @Test
    fun `Unsuccessful call is not loading and contains an exception`() = runTest {
        val error = Exception("Its all gone wrong")
        val dataSource = PopularMovieDataSource(object : MovieDbApi {
            override suspend fun getPopularMovies(apiKey: String, page: Int): MovieListResponseDto {
                throw error
            }
        })

        dataSource.refreshPopularMovies()

        assertEquals(false, dataSource.movieResult.value.isLoading)
        assertEquals(error, dataSource.movieResult.value.error)
    }

    @Test
    fun `Load more combines results lists`() = runTest {
        val results = listOf(
            MovieListDto("", 1, "", 1f, "", null, ""),
            MovieListDto("", 2, "", 1f, "", null, "")
        )
        val dataSource = PopularMovieDataSource(object : MovieDbApi {
            override suspend fun getPopularMovies(apiKey: String, page: Int): MovieListResponseDto {
                return MovieListResponseDto(page, listOf(results[page - 1]))
            }
        })

        dataSource.refreshPopularMovies()
        dataSource.loadMorePopularMovies()

        assertEquals(results[0].id, dataSource.movieResult.value.results[0].id)
        assertEquals(results[1].id, dataSource.movieResult.value.results[1].id)
    }

    @Test
    fun `Refresh clears results list`() = runTest {
        val dataSource = PopularMovieDataSource(object : MovieDbApi {
            override suspend fun getPopularMovies(apiKey: String, page: Int): MovieListResponseDto {
                return MovieListResponseDto(
                    page,
                    listOf(
                        MovieListDto("", page.toLong(), "", 1f, "", null, "")
                    )
                )
            }
        })

        dataSource.refreshPopularMovies()
        assertEquals(1, dataSource.movieResult.value.results.size)

        dataSource.loadMorePopularMovies()
        assertEquals(2, dataSource.movieResult.value.results.size)

        dataSource.refreshPopularMovies()
        assertEquals(1, dataSource.movieResult.value.results.size)
    }
}