package com.example.wewatchmvvm.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.WeWatchApplication
import com.example.wewatchmvvm.databinding.ActivitySignUpBinding
import com.example.wewatchmvvm.model.User
import com.example.wewatchmvvm.repository.RetrofitConfig
import com.example.wewatchmvvm.repository.data.UserApiCall
import com.example.wewatchmvvm.viewModel.LoginViewModel

class SignUpActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitConfig.getRetrofitBuilder()
        val userApiCall = retrofit?.create(UserApiCall::class.java)

        val loginViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return userApiCall?.let { LoginViewModel(it) } as T
            }
        })[LoginViewModel::class.java]

        loginViewModel.getHasInternetConnection().observe(this){ status ->
            if (status == MainActivity.NO_INTERNET) {
                Toast.makeText(this, MainActivity.NO_INTERNET_TEXT, Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.getUserInfo().observe(this) {
            val preference = getSharedPreferences(WeWatchApplication.SHARED_PREFERENCE, MODE_PRIVATE)
            val edit = preference.edit()

            edit.putString(WeWatchApplication.AUTH_TOKEN_KEY, it)
            edit.apply()
            Intent(applicationContext, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

        loginViewModel.getErrorList().observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEdit
            val fullName = binding.fullNameEdit
            val password = binding.passwordEdit
            val bio = binding.bioEdit

            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            val fullNameText = fullName.text.toString()
            val bioText = bio.text.toString()

            if (usernameText.isNullOrEmpty() || passwordText.isNullOrEmpty() || fullNameText.isNullOrEmpty()){
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(
                    username = usernameText,
                    password = passwordText,
                    full_name = fullNameText,
                    bio = bioText
                )
                loginViewModel.addUser(user)
            }
        }
    }
}