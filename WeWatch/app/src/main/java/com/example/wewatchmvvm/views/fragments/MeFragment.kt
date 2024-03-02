package com.example.wewatchmvvm.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.R
import com.example.wewatchmvvm.WeWatchApplication
import com.example.wewatchmvvm.databinding.FragmentMeBinding
import com.example.wewatchmvvm.model.User
import com.example.wewatchmvvm.repository.RetrofitConfig
import com.example.wewatchmvvm.repository.data.UserApiCall
import com.example.wewatchmvvm.repository.data.UserToken
import com.example.wewatchmvvm.viewModel.MeFragmentViewModel

class MeFragment : Fragment() {
    companion object {
        val TAG = "MeFragment"
    }

    private lateinit var binding: FragmentMeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(layoutInflater)
        val userToken = UserToken(requireContext())
        val token = userToken.getToken()

        val retrofit = RetrofitConfig.getRetrofitBuilder()
        val userApiCall = retrofit?.create(UserApiCall::class.java)
        val viewModel = ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return userApiCall?.let { MeFragmentViewModel(it) } as T
            }
        })[MeFragmentViewModel::class.java]

        viewModel.getHasInternetConnection().observe(viewLifecycleOwner){ status ->
            if (status == 1) {
                Toast.makeText(requireContext(), MainActivity.NO_INTERNET_TEXT, Toast.LENGTH_SHORT).show()
            }
        }

        val fullName = binding.fullNameEditor
        val username = binding.usernameEditor
        val bio = binding.bioEditor
        val editButton = binding.editProfileEdit
        val logoutButton = binding.logoutButton

        viewModel.getUserData().observe(viewLifecycleOwner) {
            fullName.setText(it.full_name)
            username.setText(it.username)
            bio.setText(it.bio)
        }

        logoutButton.setOnClickListener {
            (activity as MainActivity).logout()
        }

        viewModel.getIsEdited().observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "User edited successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "There is some problem", Toast.LENGTH_SHORT).show()
            }
        }

        editButton.setOnClickListener {
            if (fullName.text.toString().isNullOrEmpty() || username.text.toString().isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Full name and username are required", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(
                    full_name = fullName.text.toString(),
                    username = username.text.toString(),
                    bio = bio.text.toString()
                )
                if(token != null) {
                    viewModel.editUser(token, user)
                }
            }
        }

        if(token != null) {
            viewModel.getUser(token)
        }

        return binding.root
    }

}