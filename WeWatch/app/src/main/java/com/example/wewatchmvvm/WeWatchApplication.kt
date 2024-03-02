package com.example.wewatchmvvm

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast
import com.example.wewatchmvvm.views.LoginActivity

class WeWatchApplication: Application() {
    companion object {
        val AUTH_TOKEN_KEY = "we-watch-auth-token"
        val SHARED_PREFERENCE = "we-watch-shared-preference"
    }

    override fun onTerminate() {
        Toast.makeText(this, "terminated", Toast.LENGTH_SHORT).show()
        super.onTerminate()
    }

    override fun onCreate() {
        super.onCreate()
//        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
//        val token = sharedPreferences.getString(AUTH_TOKEN_KEY, "")
//        if (token == "") {
//            Intent(applicationContext, LoginActivity::class.java).apply {
//                flags = FLAG_ACTIVITY_NEW_TASK
//                startActivity(this)
//            }
//        } else {
//            Intent(applicationContext, MainActivity::class.java).apply {
//                flags = FLAG_ACTIVITY_NEW_TASK
//                startActivity(this)
//            }
//        }

    }

}