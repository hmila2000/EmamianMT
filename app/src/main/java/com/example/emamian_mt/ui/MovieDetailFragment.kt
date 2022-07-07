package com.example.emamian_mt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.emamian_mt.R
import com.example.emamian_mt.repository.MovieRepository
import com.example.emamian_mt.viewmodel.MovieDetailViewModel
import com.example.emamian_mt.viewmodel.MovieDetailViewModelFactory
import androidx.navigation.fragment.navArgs;
import com.bumptech.glide.Glide
import com.example.emamian_mt.databinding.FragmentMoviesDetailBinding
import com.example.emamian_mt.databinding.FragmentPopularMoviesBinding
import com.example.emamian_mt.util.Constants
import com.example.emamian_mt.viewmodel.MovieViewModelFactory
import com.example.emamian_mt.viewmodel.PopularMoviesViewModel

class MovieDetailFragment : Fragment(R.layout.fragment_movies_detail) {
    lateinit var viewModel: MovieDetailViewModel
    val args: MovieDetailFragmentArgs by navArgs()
    private var binding: FragmentMoviesDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoviesDetailBinding.inflate(layoutInflater, container, false);
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let {
            it.title.text = args.movie.title
            it.overview.text = args.movie.overview
            Glide.with(view).load(Constants.BASE_URL_IMAGE_PATH + args.movie.backdrop_path)
                .into(it.image)
            it.title.text = args.movie.title
        }
        val movieRepository = MovieRepository()
        val viewModelProviderFactory = MovieDetailViewModelFactory(
            requireActivity().application,
            movieRepository,
            args.movie.id
        )
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MovieDetailViewModel::class.java)
        viewModel.MovieDetail.observe(viewLifecycleOwner) {
            it.data?.let { data ->

                binding?.let { binding ->
                    var genreText: String = ""
                    data.genres.forEach {
                        genreText += it.name
                        binding.genre.text = binding.genre.text.toString() + " , " + it.name
                    }
                    if (genreText.isNotEmpty()) {
                        genreText = "genre: $genreText"
                        binding.genre.text = genreText
                    }
                    var countriesText:String = ""
                    data.production_countries.forEach {
                        countriesText += it.name
                        binding.country.text = binding.country.text.toString() + " , " + it.name
                    }
                    if (countriesText.isNotEmpty()) {
                        countriesText = "countries: $countriesText"
                        binding.country.text = countriesText
                    }
                    binding.release.text = "release data: " + data.release_date
                }
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}