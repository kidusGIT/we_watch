package com.example.wewatchmvvm.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConfig {
    companion object {
        private const val IP = "192.168.8.148"
        fun getRetrofitBuilder(): Retrofit? = Retrofit.Builder()
            .baseUrl("http://$IP:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}