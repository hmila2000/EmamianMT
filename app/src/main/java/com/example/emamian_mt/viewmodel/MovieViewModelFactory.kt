package com.example.emamian_mt.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.emamian_mt.repository.MovieRepository

class MovieViewModelFactory(
    val app: Application,
    val movieRepository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PopularMoviesViewModel(app, movieRepository) as T
    }
}