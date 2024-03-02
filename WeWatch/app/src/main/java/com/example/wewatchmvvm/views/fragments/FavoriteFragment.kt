package com.example.wewatchmvvm.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.database.repository.MovieItemAdapter
import com.example.wewatchmvvm.databinding.FragmentFavoriteBinding
import com.example.wewatchmvvm.viewModel.FavoriteViewModel
import com.example.wewatchmvvm.views.MovieAdapter

class FavoriteFragment : Fragment() {
    companion object {
        val TAG = "FavoriteFragment"
    }

    lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        val recyclerView = binding.favoriteRecyclerView
        val repository = (activity as MainActivity).movieRepository

        val favoriteViewModel: FavoriteViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoriteViewModel(repository!!) as T
            }
        })[FavoriteViewModel::class.java]

        favoriteViewModel.getAllMovies().observe(viewLifecycleOwner) {
            val adapter = MovieItemAdapter(requireContext(), it){ movieId ->
                val detailMovieFragment = DetailMovieFragment()
                val bundle = Bundle()
                bundle.putInt("favoriteMovieId", movieId)
                detailMovieFragment.arguments = bundle

                (activity as MainActivity).replaceFragment(
                    detailMovieFragment,
                    DetailMovieFragment.TAG,
                    TAG,
                    true
                )
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

}