package com.example.emamian_mt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.emamian_mt.R
import com.example.emamian_mt.models.Movie
import com.example.emamian_mt.util.Constants

class PopularMoviesAdapter : RecyclerView.Adapter<PopularMoviesAdapter.MoviesViewHolder>() {

    inner class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.mvImage)
        val title: TextView = itemView.findViewById(R.id.mvTitle)
        val overView: TextView = itemView.findViewById(R.id.mvOverView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularMoviesAdapter.MoviesViewHolder {
        return MoviesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_movies_popular,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PopularMoviesAdapter.MoviesViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.apply {
            Glide.with(itemView).load(Constants.BASE_URL_IMAGE_PATH+movie.backdrop_path).into(image)
            title.text = movie.original_title
            overView.text=movie.overview

            itemView.apply {
                setOnClickListener {
                    onItemClickListener?.let { it(movie) }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Movie) -> Unit)? = null

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onItemClickListener = listener
    }
}