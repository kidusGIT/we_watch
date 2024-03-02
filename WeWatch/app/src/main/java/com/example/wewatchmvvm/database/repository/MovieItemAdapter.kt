package com.example.wewatchmvvm.database.repository

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wewatchmvvm.R
import com.example.wewatchmvvm.database.model.MovieItem
import com.example.wewatchmvvm.databinding.MovieItemMainBinding
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.views.fragments.MyMovieFragmentFunc

class MovieItemAdapter(private val context: Context, private val list: List<MovieItem>, private val onClick: (id: Int) -> Unit): RecyclerView.Adapter<MovieItemAdapter.MovieViewHolder>() {
    private val TAG = "MovieItemAdapter"
    var adapterMovieInterface: MyMovieFragmentFunc? = null

    inner class MovieViewHolder(val binding: MovieItemMainBinding ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = MovieItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = list[position]

        holder.binding.movieName.text = movie.title
        holder.binding.movieDescrption.text = if(movie.description!!.length >= 20) movie.description!!.subSequence(0, 20) else movie.description

        holder.binding.movieItem.setOnClickListener {
            movie.movie_id?.let { it1 -> onClick(it1) }
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

    override fun getItemCount() = list.size
}