package com.example.wewatchmvvm.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.databinding.FragmentMovieBinding
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.RetrofitConfig
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.viewModel.MoviesViewModel
import com.example.wewatchmvvm.views.MovieAdapter

class MovieFragment : Fragment(), MyMovieFragmentFunc {
    companion object {
        val TAG = "MovieFragment"
    }

    private lateinit var binding: FragmentMovieBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var statusText: TextView

    private var moviesViewModel: MoviesViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieBinding.inflate(layoutInflater)
        recyclerView = binding.movieRecyclerView

        statusText = binding.moviesStatusText
        progress = binding.mainMoviesProgress

        val retrofit = RetrofitConfig.getRetrofitBuilder()
        val moviesApiCall = retrofit?.create(MovieApiCall::class.java)!!
        val repository = (activity as MainActivity).movieRepository!!
        moviesViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MoviesViewModel(moviesApiCall, repository) as T
            }
        })[MoviesViewModel::class.java]

        moviesViewModel!!.getAllCachedMovies().observe(viewLifecycleOwner){
            if (it != null) {
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                statusText.visibility = View.GONE

                val adapter = MovieAdapter(requireActivity().applicationContext, it,)
                adapter.saveData(it)
                adapter.adapterMovieInterface = this
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        moviesViewModel!!.getMoviesList().observe(viewLifecycleOwner) {
            if (it != null) {
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                statusText.visibility = View.GONE

                moviesViewModel!!.deleteCachedMovies()
                moviesViewModel!!.insertAllCachedMovie(it)

                val adapter = MovieAdapter(requireActivity().applicationContext, it, )
                adapter.saveData(it)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        moviesViewModel!!.getIsThereConnection().observe(viewLifecycleOwner) {
            when (it) {
                MainActivity.HAS_INTERNET -> {

                }
                MainActivity.NO_INTERNET -> {
                    progress.visibility = View.GONE
                    statusText.visibility = View.VISIBLE
                    statusText.text = MainActivity.NO_INTERNET_TEXT
                }
            }
        }

        val sharedClass = (activity as MainActivity).sharedClass
        sharedClass.setOnDataChangedListener(TAG, object: SharedClass.OnDataChangedListener{
            override fun onDataChangedListener() {
                Log.d("SharedManagerTag", "Movie Fragment data changed")
                moviesViewModel?.getMovies()
            }
        })

       moviesViewModel?.getMovies()
       return binding.root
    }

    override fun onPressCard(id: Int) {
        val detailMovieFragment = DetailMovieFragment()
        val bundle = Bundle()
        bundle.putInt("id", id)
        detailMovieFragment.arguments = bundle
        (activity as MainActivity).replaceFragment(detailMovieFragment, DetailMovieFragment.TAG, TAG, true)
    }

    override fun deleteMovie(id: Int, position: Int, list: List<Movie>) {
        TODO("Not yet implemented")
    }

}