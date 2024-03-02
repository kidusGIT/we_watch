package com.example.wewatchmvvm.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.Permissions
import com.example.wewatchmvvm.R
import com.example.wewatchmvvm.databinding.FragmentAddMovieBinding
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.RetrofitConfig
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.repository.data.UserToken
import com.example.wewatchmvvm.viewModel.AddMoviesViewModel

class AddMovieFragment(private val id: Int? = null) : Fragment() {
    companion object {
        val TAG = "AddMovieFragment"
        val TAG_EDIT = "AddMovieFragmentEdit"
    }

    private lateinit var binding: FragmentAddMovieBinding
    private var moviesViewModel: AddMoviesViewModel? = null

    private lateinit var movieImageView: ImageView
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var addMovie: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMovieBinding.inflate(layoutInflater)

        val retrofit = RetrofitConfig.getRetrofitBuilder()
        val movieApiCall = retrofit?.create(MovieApiCall::class.java)!!
        val repository = (activity as MainActivity).movieRepository!!

        if (moviesViewModel == null) {
            moviesViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddMoviesViewModel(movieApiCall, repository) as T
                }
            })[AddMoviesViewModel::class.java]
        }

        moviesViewModel!!.getIsThereConnection().observe(viewLifecycleOwner){ status ->
                if (status == MainActivity.NO_INTERNET) {
                    Toast.makeText(requireContext(), MainActivity.NO_INTERNET_TEXT, Toast.LENGTH_SHORT).show()
                }
        }

        movieImageView = binding.addMovieImage
        movieImageView.clipToOutline = true

        title = binding.titleEdit
        description = binding.descriptionEdit
        addMovie = binding.addMovieButton

        moviesViewModel!!.getMovieLiveData().observe(viewLifecycleOwner){ movie ->
            addMovie.text = "Edit movie"
            title.setText(movie.title!!)
            description.setText(movie.description!!)

            if (movie.cover_url == null) movieImageView.setImageResource(R.drawable.movie_image)
            else Glide.with(requireContext())
                .load(movie.cover_url!!)
                .into(movieImageView)

        }

        if (id != null) {
            moviesViewModel!!.getCachedMovie(id).observe(viewLifecycleOwner){ movie ->
                if (movie != null) {
                    addMovie.text = "Edit movie"
                    title.setText(movie.title!!)
                    description.setText(movie.description!!)

                    if (movie.cover_url == null) movieImageView.setImageResource(R.drawable.movie_image)
                    else Glide.with(requireContext())
                        .load(movie.cover_url!!)
                        .into(movieImageView)
                }
            }
            moviesViewModel!!.getMovie(id)
        }

        moviesViewModel?.getHasMovieAdd()?.observe(viewLifecycleOwner){
             if (it == true) {
                 val listener = (activity as MainActivity).sharedClass.hasListener

                 listener[MyMovieFragment.TAG]?.onDataChangedListener()
                 listener[MovieFragment.TAG]?.onDataChangedListener()
             }
        }

        addMovie.setOnClickListener {
            addEditMovie(title = title.text.toString(), desc = description.text.toString())
        }

        if (moviesViewModel?.uri != null) {
            movieImageView.setImageURI(moviesViewModel?.uri)
        }

        movieImageView.setOnClickListener {
            val permissions = Permissions(requireContext())
            val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

            if (permissions.checkPermission(permission)) {
                chooseImage()
            } else {
                (activity as MainActivity).requestAPermission(permission) {
                    chooseImage()
                }
            }
        }
        return binding.root
    }

    private fun addEditMovie(title: String, desc: String) {
        val userToken = UserToken(requireContext())
        val token = userToken.getToken()
        val id = userToken.getId()

        if (title.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
        } else {

            if (token != null && this.id == null) {
                val movie = Movie(
                    title = title,
                    description = desc,
                    user_id = id
                )
                moviesViewModel!!.createMovie(requireContext(), movie, token!!)
            } else if (token != null && this.id != null && this.id > 0) {
                val movie = moviesViewModel!!.editMovie
                movie?.title = title
                movie?.description = desc
                moviesViewModel!!.editMovie(requireContext(), movie!!, token!!)
            }
        }
    }

    private fun chooseImage() {
        (activity as MainActivity).selectImage {
            moviesViewModel?.uri = it
            movieImageView.setImageURI(moviesViewModel?.uri)
        }
    }

}