package com.example.wewatchmvvm.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.MainActivityFunc
import com.example.wewatchmvvm.R
import com.example.wewatchmvvm.database.model.MovieItem
import com.example.wewatchmvvm.databinding.FragmentMovieDetailBinding
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.RetrofitConfig
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.viewModel.AddMoviesViewModel
import com.example.wewatchmvvm.viewModel.FavoriteViewModel

class DetailMovieFragment() : Fragment() {
    companion object {
        val TAG = "DetailMovieFragment"
    }

    private lateinit var binding: FragmentMovieDetailBinding

    private lateinit var favoriteButton: ImageButton

    private var addMovieViewModel: AddMoviesViewModel? = null
    private var movie: Movie? = null

    private var isToggled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(layoutInflater)
        val movieId = arguments?.getInt("id")
        val favoriteMovieId = arguments?.getInt("favoriteMovieId")

        var mainId = 0
        if (movieId == 0 || movieId == null) mainId = favoriteMovieId!!

        if (favoriteMovieId == 0 || favoriteMovieId == null) mainId = movieId!!

        val retrofit = RetrofitConfig.getRetrofitBuilder()
        val movieApiCall = retrofit?.create(MovieApiCall::class.java)!!
        val repository = (activity as MainActivity).movieRepository!!

        addMovieViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddMoviesViewModel(movieApiCall, repository) as T
            }
        })[AddMoviesViewModel::class.java]

        binding.movieDetailContainer.setOnClickListener {  }
        favoriteButton = binding.favoriteButton

//        val favoriteViewModel =
        val favoriteViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoriteViewModel(repository) as T
            }
        })[FavoriteViewModel::class.java]

        if (mainId != null) {
            favoriteViewModel.getMovie(mainId).observe(viewLifecycleOwner) { movie ->
                favoriteViewModel.movieItem = movie
                isToggled = if (movie == null) {
                    false
                } else {
                    animateButton(true)
                    true
                }
            }
        }

        favoriteButton.setOnClickListener {
            if (this.movie == null) return@setOnClickListener

            if (isToggled) {
                val movieItem = favoriteViewModel.movieItem
                if (movieItem != null) {
                    favoriteViewModel.delete(movieItem)
                    animateButton(false)
                }
            } else {
                val movieItem = movieId?.let { it1 ->
                    MovieItem(
                        title = movie?.title!!,
                        description = movie?.description!!,
                        cover_url = movie?.cover_url!!,
                        user_id = movie?.user_id!!,
                        movie_id = it1
                    )
                }
                if (movieItem != null) {
                    favoriteViewModel.insert(movieItem)
                }
            }
        }

        addMovieViewModel!!.getMovieLiveData().observe(viewLifecycleOwner){ movie ->
            if (movie != null) {
                binding.movieDetailCover.clipToOutline = true
                this.movie = movie
                if (movie.cover_url == null) {
                    binding.movieDetailCover.setImageResource(R.drawable.movie_image)
                } else {
                    if (movie.cover_url == null) {
                        binding.movieDetailCover.setBackgroundResource(R.drawable.movie_image)
                    } else {
                        Glide.with(requireContext())
                            .load(movie.cover_url)
                            .into(binding.movieDetailCover)
                    }
                }

                binding.movieTitle.text = movie.title
                binding.movieDetailDescription.text = if (movie.description == null || movie.description == "") "No description" else movie.description
            }
        }

        if (mainId != null && mainId > 0) {
            addMovieViewModel!!.getCachedMovie(mainId).observe(viewLifecycleOwner){ mov ->
                if (mov != null) {
                    binding.movieDetailCover.clipToOutline = true

                    if (mov.cover_url == null) {
                        binding.movieDetailCover.setImageResource(R.drawable.movie_image)
                    } else {
                        if (mov.cover_url == null) {
                            binding.movieDetailCover.setBackgroundResource(R.drawable.movie_image)
                        } else {
                            Glide.with(requireContext())
                                .load(mov.cover_url)
                                .into(binding.movieDetailCover)
                        }
                    }

                    binding.movieTitle.text = mov.title
                    binding.movieDetailDescription.text = if (mov.description == null || mov.description == "") "No description" else mov.description
                }
            }
            addMovieViewModel!!.getMovie(mainId)
        }

        return binding.root
    }

    private fun animateButton(isToggled: Boolean) {
        val it = favoriteButton

        if (isToggled) {
            it.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
            it.animate().apply {
                duration = 200
                scaleY(1.2f)
                scaleX(1.2f)
            }.withEndAction {
                it.animate().apply {
                    duration = 200
                    scaleY(1f)
                    scaleX(1f)
                }
            }
        } else {
            it.setBackgroundResource(R.drawable.favorite_24)
        }
    }

}