package ie.redstar.moviedb.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ie.redstar.moviedb.domain.model.MovieListModel
import kotlinx.coroutines.flow.MutableStateFlow

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class State {
        data class Loaded(val movie: MovieListModel) : State()
    }

    private val movie = savedStateHandle.get<MovieListModel>("movie")!!
    private val _state = MutableStateFlow<State>(State.Loaded(movie))
    val state: LiveData<State> = _state.asLiveData()
}