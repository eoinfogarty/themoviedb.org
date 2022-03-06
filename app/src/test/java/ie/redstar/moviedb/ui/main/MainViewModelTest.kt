package ie.redstar.moviedb.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ie.redstar.moviedb.domain.model.MovieListModel
import ie.redstar.moviedb.domain.model.MovieListResultModel
import ie.redstar.moviedb.domain.repository.PopularMovieRepository
import ie.redstar.moviedb.util.MainCoroutineRule
import ie.redstar.moviedb.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @Rule
    @JvmField
    var testRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val resultModel = MovieListResultModel(
        results = listOf(MovieListModel("", 1, "", 1, "", "", ""))
    )
    private val emptyModel = MovieListResultModel(
        results = listOf()
    )
    private val loadMoreModel = MovieListResultModel(
        results = listOf(MovieListModel("", 2, "", 1, "", "", ""))
    )
    private val errorModel = MovieListResultModel(
        error = Exception(),
        results = listOf(MovieListModel("", 2, "", 1, "", "", ""))
    )
    private val emptyErrorModel = MovieListResultModel(
        error = Exception(),
        results = listOf()
    )

    @Test
    fun `Data source with values gives an loaded state`() = runTest {
        val dataSource = FakePopularDataSource(resultModel, loadMoreModel)
        val viewModel = MainViewModel(dataSource)
        val state = viewModel.state.getOrAwaitValue {
            advanceUntilIdle()
        }

        assertEquals(state, MainViewModel.State.Loaded(resultModel))
    }

    @Test
    fun `Data source with no values gives an empty state`() = runTest {
        val dataSource = FakePopularDataSource(emptyModel, loadMoreModel)
        val viewModel = MainViewModel(dataSource)
        val state = viewModel.state.getOrAwaitValue {
            advanceUntilIdle()
        }

        assertEquals(state, MainViewModel.State.Empty)
    }

    @Test
    fun `Data source with exception and no values gives an error state`() = runTest {
        val dataSource = FakePopularDataSource(emptyErrorModel, loadMoreModel)
        val viewModel = MainViewModel(dataSource)
        val state = viewModel.state.getOrAwaitValue {
            advanceUntilIdle()
        }

        assertEquals(state, MainViewModel.State.Error)
    }

    @Test
    fun `Data source with exception and values gives a loaded state with error event`() = runTest {
        val dataSource = FakePopularDataSource(errorModel, loadMoreModel)
        val viewModel = MainViewModel(dataSource)
        val state = viewModel.state.getOrAwaitValue {
            advanceUntilIdle()
        }

        assertEquals(viewModel.events.first(), MainViewModel.Event.ShowErrorToast)
        assertEquals(state, MainViewModel.State.Loaded(errorModel))
    }

    @Test
    fun `Load more adds to result list`() = runTest {
        val dataSource = FakePopularDataSource(resultModel, loadMoreModel)
        val viewModel = MainViewModel(dataSource)

        viewModel.loadMoreMovies()

        val state = viewModel.state.getOrAwaitValue {
            advanceUntilIdle()
        }

        val expected = resultModel.copy(
            results = resultModel.results + loadMoreModel.results
        )
        assertEquals(state, MainViewModel.State.Loaded(expected))
    }

    class FakePopularDataSource(
        private val refreshValue: MovieListResultModel,
        private val loadMoreModel: MovieListResultModel,
    ) : PopularMovieRepository {

        private val _fakeFlow = MutableStateFlow(MovieListResultModel(isLoading = true))

        override val movieResult: StateFlow<MovieListResultModel>
            get() = _fakeFlow.asStateFlow()

        override suspend fun loadMorePopularMovies() {
            _fakeFlow.emit(
                refreshValue.copy(
                    results = refreshValue.results + loadMoreModel.results
                )
            )
        }

        override suspend fun refreshPopularMovies() {
            _fakeFlow.emit(refreshValue)
        }
    }
}