package com.example.emamian_mt.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.emamian_mt.repository.MovieRepository

class MovieDetailViewModelFactory(
    val app: Application,
    val movieRepository: MovieRepository,
    val movieId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieDetailViewModel(app, movieRepository, movieId) as T
    }
}