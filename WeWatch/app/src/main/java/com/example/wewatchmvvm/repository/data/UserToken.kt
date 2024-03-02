package com.example.wewatchmvvm.repository.data

import android.content.Context
import android.util.Log
import com.example.wewatchmvvm.WeWatchApplication

class UserToken(private val context: Context) {
    private val TAG = "UserToken"

    fun getToken(): String? {
        val preferences = context.getSharedPreferences(WeWatchApplication.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val data = preferences.getString(WeWatchApplication.AUTH_TOKEN_KEY, "")
        if (data != "" && data != null) {
            val tokenArray = data.split('-')
            Log.d(TAG, "getToken array: $tokenArray")
            var token = tokenArray[1]
            for (index in tokenArray.indices) {
                if (index == 0 || index == 1) continue
                token = "$token-${tokenArray[index]}"
            }
            Log.i(TAG, "getToken: $token")
            return "token $token"
        }

        return null
    }

    fun getId(): Int {
        val preferences = context.getSharedPreferences(WeWatchApplication.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val data = preferences.getString(WeWatchApplication.AUTH_TOKEN_KEY, "")
        if (data != "" && data != null) {
            return data.split('-')[0].toInt()
        }

        return 0
    }

}