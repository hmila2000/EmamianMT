package com.example.emamian_mt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emamian_mt.R
import com.example.emamian_mt.adapter.PopularMoviesAdapter
import com.example.emamian_mt.databinding.FragmentPopularMoviesBinding
import com.example.emamian_mt.repository.MovieRepository
import com.example.emamian_mt.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.emamian_mt.util.Resource
import com.example.emamian_mt.viewmodel.PopularMoviesViewModel
import com.example.emamian_mt.viewmodel.MovieViewModelFactory

class PopularMoviesFragment : Fragment(R.layout.fragment_popular_movies) {
    lateinit var viewModel: PopularMoviesViewModel
    private lateinit var moviesAdapter: PopularMoviesAdapter
    private var binding: FragmentPopularMoviesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopularMoviesBinding.inflate(layoutInflater, container, false);
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieRepository = MovieRepository()
        val viewModelProviderFactory = MovieViewModelFactory(requireActivity().application, movieRepository)
        viewModel =  ViewModelProvider(this, viewModelProviderFactory).get(PopularMoviesViewModel::class.java)
        setupRecyclerView()
        moviesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_popularMoviesFragment_to_moviesDetailFragment,
                bundle
            )
        }

        viewModel.popularMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { MoviesResponse ->
                        moviesAdapter.differ.submitList(MoviesResponse.results.toList())
                        val totalPages = MoviesResponse.total_pages / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.popularMoviesPage == totalPages
                        if(isLastPage) {
                            binding!!.rvPopularMovies.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        println("error: $message")
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        binding!!.itemErrorMessage.btnRetry.setOnClickListener {
            viewModel.getPopularMovies()
        }

    }

    private fun hideProgressBar() {
        binding?.let {
            it.paginationProgressBar.visibility = View.INVISIBLE
            isLoading = false
        }
    }

    private fun showProgressBar() {
        binding?.let {
            it.paginationProgressBar.visibility = View.VISIBLE
            isLoading = true
        }
    }

    private fun hideErrorMessage() {
        binding?.let {
            it.itemErrorMessage.root.visibility = View.INVISIBLE
            isError = false
        }
    }

    private fun showErrorMessage(message: String) {
        binding?.let {
            it.itemErrorMessage.root.visibility = View.VISIBLE
            it.itemErrorMessage.tvErrorMessage.text = message
            isError = true
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getPopularMovies()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        moviesAdapter = PopularMoviesAdapter()
        binding?.let {
            it.rvPopularMovies.apply {
                adapter = moviesAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@PopularMoviesFragment.scrollListener)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}