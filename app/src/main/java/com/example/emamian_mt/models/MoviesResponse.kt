package com.example.emamian_mt.models

data class MoviesResponse(
    val results: MutableList<Movie>,
    val page: Int,
    val totalResults: Int,
    val total_pages: Int
)