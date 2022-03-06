package ie.redstar.moviedb.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.redstar.moviedb.domain.model.MovieListResultModel
import ie.redstar.moviedb.domain.repository.PopularMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val popularMovieRepository: PopularMovieRepository
) : ViewModel() {

    sealed class State {
        object Empty : State()
        object Error : State()
        object Loading : State()
        object LoadingMore : State()
        data class Loaded(val movieResult: MovieListResultModel) : State()
    }

    sealed class Event {
        object ShowErrorToast : Event()
    }

    val state: LiveData<State> = popularMovieRepository.movieResult.map { result ->
        when {
            result.isLoading && result.results.isNotEmpty() -> {
                State.LoadingMore
            }
            result.isLoading -> {
                State.Loading
            }
            result.error != null && result.results.isEmpty() -> {
                State.Error
            }
            result.error != null && result.results.isNotEmpty() -> {
                _events.send(Event.ShowErrorToast)
                State.Loaded(result)
            }
            result.results.isEmpty() -> {
                State.Empty
            }
            else -> {
                State.Loaded(result)
            }
        }
    }.asLiveData()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        refreshMovies()
    }

    fun refreshMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            popularMovieRepository.refreshPopularMovies()
        }
    }

    fun loadMoreMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            popularMovieRepository.loadMorePopularMovies()
        }
    }
}