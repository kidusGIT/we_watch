package com.example.wewatchmvvm.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.databinding.FragmentMyMovieBinding
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.RetrofitConfig
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.repository.data.UserToken
import com.example.wewatchmvvm.viewModel.MyMoviesViewModel
import com.example.wewatchmvvm.views.MovieAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyMovieFragment : Fragment(), MyMovieFragmentFunc {
    companion object {
        val TAG = "MyMovieFragment"
    }

    private lateinit var binding: FragmentMyMovieBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var addMovie: FloatingActionButton

    private var viewModel: MyMoviesViewModel? = null
    private var adapter: MovieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyMovieBinding.inflate(layoutInflater)

        recyclerView = binding.myMovieRecyclerView
        progress = binding.myMovieProgress
        addMovie = binding.addMovieFloatingButton

        val retrofit = RetrofitConfig.getRetrofitBuilder()
        val moviesApiCall = retrofit?.create(MovieApiCall::class.java)!!
        val cachedMovieRecyclerView = (activity as MainActivity).movieRepository!!

        viewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MyMoviesViewModel(moviesApiCall, cachedMovieRecyclerView) as T
            }
        })[MyMoviesViewModel::class.java]

        val userToken = UserToken(requireContext())
        val id = userToken.getId()
        val data = userToken.getToken()

        viewModel!!.getIsThereConnection().observe(viewLifecycleOwner) {
            when (it) {
                MainActivity.NO_INTERNET -> {
                    Toast.makeText(requireContext(), MainActivity.NO_INTERNET_TEXT, Toast.LENGTH_SHORT).show()
                }
            }
        }

        addMovie.setOnClickListener {
            val addMovieFragment = AddMovieFragment()
            (activity as MainActivity).replaceFragment(addMovieFragment, AddMovieFragment.TAG, TAG, true)
        }

        viewModel!!.getMyMovies().observe(viewLifecycleOwner){
            if(it != null) {
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                adapter = MovieAdapter(requireContext(), it, true)
                adapter!!.saveData(it)

                adapter!!.adapterMovieInterface = this
                recyclerView.adapter = adapter!!
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            } else {
                progress.visibility = View.GONE
            }
        }

        viewModel!!.getPositionLiveData().observe(viewLifecycleOwner){
            adapter!!.notifyItemRemoved(it)
            val listener = (activity as MainActivity).sharedClass.hasListener[MovieFragment.TAG]
            listener?.onDataChangedListener()
        }

        if (id > 0) {
            viewModel!!.getMyCachedMovies(id).observe(viewLifecycleOwner){
                if (it != null) {
                    progress.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    adapter = MovieAdapter(requireContext(), it, true)
                    adapter!!.saveData(it)
                    adapter!!.adapterMovieInterface = this
                    recyclerView.adapter = adapter!!
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }

        if(data != "" && data != null) {
            viewModel!!.getMyMovies(data)

            val sharedClass = (activity as MainActivity).sharedClass
            sharedClass.setOnDataChangedListener(TAG, object: SharedClass.OnDataChangedListener{
                override fun onDataChangedListener() {
                    viewModel?.getMyMovies("token $data")
                }
            })
        }

        return binding.root
    }

    override fun onPressCard(id: Int) {
        val addMovieFragment = AddMovieFragment(id = id)
        (activity as MainActivity).replaceFragment(addMovieFragment, AddMovieFragment.TAG, TAG, true)
    }

    override fun deleteMovie(id: Int, position: Int, list: List<Movie>) {
        val userInfo = UserToken(requireContext())
        val token = userInfo.getToken()
        if (token != null && token != "") {
            viewModel!!.deleteMovie(token, id, position, list as ArrayList<Movie>)
        }
    }

}