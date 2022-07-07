package com.example.emamian_mt.models

import java.io.Serializable

data class Movie(
    var poster_path: String? = null,
    val adult: Boolean,
    val overview: String,
    val release_date: String,
    val genre_ids: MutableList<Int>,
    val id: Int,
    val original_title: String,
    val original_language: String,
    val title: String,
    val backdrop_path: String?,
    val popularity: Number,
    val vote_count: Int,
    val video: Boolean,
    val vote_average: Number
) : Serializable