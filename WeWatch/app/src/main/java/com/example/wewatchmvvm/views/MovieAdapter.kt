package com.example.wewatchmvvm.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wewatchmvvm.R
import com.example.wewatchmvvm.database.model.MovieItem
import com.example.wewatchmvvm.databinding.MovieItemMainBinding
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.views.fragments.MyMovieFragmentFunc

class MovieAdapter(private val context: Context, private val list: List<Movie>, private val isUser: Boolean = false): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private val TAG = "MovieAdapter"
    var adapterMovieInterface: MyMovieFragmentFunc? = null

    inner class MovieViewHolder(val binding: MovieItemMainBinding ): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun saveData(movies: List<Movie>) {
        differ.submitList(movies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = MovieItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
//        val movie = list[position]
        val movie = differ.currentList[position]

        holder.binding.movieName.text = movie.title
        holder.binding.movieDescrption.text = if(movie.description!!.length >= 20) movie.description!!.subSequence(0, 20) else movie.description

        if (isUser) {
            holder.binding.deleteButton.visibility = View.VISIBLE
            holder.binding.deleteButton.setOnClickListener {
                adapterMovieInterface?.deleteMovie(movie.id!!, position, list)
            }
        }

        holder.binding.movieItem.setOnClickListener {
            adapterMovieInterface?.onPressCard(movie.id!!)
        }

        holder.binding.movieCover.clipToOutline = true
        if (movie.cover_url == null) {
            holder.binding.movieCover.setImageResource(R.drawable.movie_image)
        } else {
            Glide.with(context)
                .load(movie.cover_url)
                .into(holder.binding.movieCover)
        }

    }

    override fun getItemCount() = differ.currentList.size
}