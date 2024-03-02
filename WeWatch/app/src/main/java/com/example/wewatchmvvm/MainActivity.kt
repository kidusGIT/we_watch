package com.example.wewatchmvvm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wewatchmvvm.database.repository.MovieRepository
import com.example.wewatchmvvm.databinding.ActivityMainBinding
import com.example.wewatchmvvm.viewModel.MainActivityViewModel
import com.example.wewatchmvvm.views.LoginActivity
import com.example.wewatchmvvm.views.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Stack
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityDebugTag"
//    constants
    companion object {
        val NO_INTERNET = 0
        val HAS_INTERNET = 1
        val HAS_SLOW_INTERNET = 2

        val NO_INTERNET_TEXT = "Please connect to the internet"
        val SLOW_INTERNET_TEXT = "Your connect is slow"
    }

    private val fragmentManager = supportFragmentManager
    private val mainFragments = hashMapOf(
        MyMovieFragment.TAG to MyMovieFragment(),
        MovieFragment.TAG to MovieFragment(),
        FavoriteFragment.TAG to FavoriteFragment(),
        MeFragment.TAG to MeFragment()
    )

    private var mainActivityViewModel: MainActivityViewModel? = null

    private lateinit var binding: ActivityMainBinding
    private val permissions: Permissions by lazy {
        Permissions(this)
    }

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    var sharedClass: SharedClass = SharedClass()
    var movieRepository: MovieRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = applicationContext.getSharedPreferences(WeWatchApplication.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(WeWatchApplication.AUTH_TOKEN_KEY, "")
        if (token == "") {
            Intent(applicationContext, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(this)
            }
           finish()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener { p0, p1 ->
            val token = p0?.getString(p1, "")
            Log.d(TAG, "onSharedPreferenceChanged: $token")
            if (token == ""){
                Intent(this, LoginActivity::class.java).apply {
                    startActivity(this)
                }
                finish()
            }

            Toast.makeText(this@MainActivity, "$token", Toast.LENGTH_SHORT).show()
        }

        mainActivityViewModel = ViewModelProvider(this@MainActivity, object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainActivityViewModel(this@MainActivity, mainFragments) as T
                }
        })[MainActivityViewModel::class.java]

        movieRepository = MovieRepository(this)

        mainActivityViewModel?.fragmentManager = supportFragmentManager

        if (mainActivityViewModel?.hasRotated == false) mainActivityViewModel?.navigate(mainFragments[MovieFragment.TAG]!!, MovieFragment.TAG)

        sharedClass = SharedClass()

//        permissions
        permissions.initializeLauncher()
        permissions.initializeImageLauncher()

        bottomNavigation = binding.bottomNavigationView
        frameLayout = binding.mainFrameLayer

        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.movies_menu -> {
                    replaceFragment(mainFragments[MovieFragment.TAG]!!, MovieFragment.TAG)
                    return@setOnItemSelectedListener true
                }
                R.id.favorite_menu -> {
                    replaceFragment(mainFragments[FavoriteFragment.TAG]!!, FavoriteFragment.TAG)
                    return@setOnItemSelectedListener true
                }
                R.id.my_movies_menu -> {
                    val myMovieFragment = MyMovieFragment()
                    replaceFragment(mainFragments[MyMovieFragment.TAG]!!, MyMovieFragment.TAG)
                    return@setOnItemSelectedListener true
                }
                R.id.me_menu -> {
                    replaceFragment(mainFragments[MeFragment.TAG]!!, MeFragment.TAG)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onDestroy() {
        mainActivityViewModel?.hasRotated = true
        super.onDestroy()
    }

    override fun onBackPressed() {
        val isLast = mainActivityViewModel?.popBackFragment()

        if (isLast == true) {
            finish()
//            exitProcess(0)
        }
    }

    fun replaceFragment(newFragment: Fragment, tag: String, rootTag:String? = null, isBackStacked: Boolean= false) {
        mainActivityViewModel?.navigate(newFragment, tag, rootTag, isBackStacked)
    }

    fun requestAPermission(permission: String, callback: () -> Unit) {
        permissions.requestPermission(permission, callback)
    }

    fun logout() {
        val preference = getSharedPreferences(WeWatchApplication.SHARED_PREFERENCE, MODE_PRIVATE)
        val edit = preference.edit()
        edit.remove(WeWatchApplication.AUTH_TOKEN_KEY)
        edit.apply()

        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
        }
        finish()
    }

    fun selectImage(callback: (uri: Uri) -> Unit) {
        Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            permissions.launchImageLauncher(this, callback)
        }
    }

}