package com.example.emamian_mt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.emamian_mt.repository.MovieRepository
import com.example.emamian_mt.viewmodel.MovieViewModelFactory

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val movieRepository = MovieRepository()
        val viewModelProviderFactory = MovieViewModelFactory(application, movieRepository)


    }
}