package com.example.lab_week_13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    // define the StateFlow in replace of the LiveData
// a StateFlow is an observable Flow that emits state updates to the collectors
// MutableStateFlow is a StateFlow that you can change the value
    init {
        fetchPopularMovies()
    }
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> get() = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> get() = _error

    // fetch movies from the API
    private fun fetchPopularMovies() {
        // launch a coroutine in viewModelScope
        // Dispatchers.IO means that this coroutine will run on a shared pool of threads
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                // catch is an operator that catches exceptions from the Flow
                .catch { e ->
                    _error.value = "An exception occurred: ${e.message}"
                }
                // collect is a terminal operator that collects the values from the Flow
                // the results are emitted to the StateFlow
                .collect { movies ->
                    _popularMovies.value = movies
                }
        }
    }
}