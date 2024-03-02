package com.example.wewatchmvvm.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.model.User
import com.example.wewatchmvvm.model.UserInfo
import com.example.wewatchmvvm.repository.repository.UserRepository
import com.example.wewatchmvvm.repository.data.UserApiCall
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class LoginViewModel(userApiCall: UserApiCall): ViewModel() {
    private val TAG = "LoginViewModel"
    private var userRepository: UserRepository = UserRepository(userApiCall)

    private val userInfo = MutableLiveData<String>()
    private val hasInternetConnection = MutableLiveData<Int>()
    private val errorList = MutableLiveData<String>()

    fun getUserInfo() = userInfo
    fun getErrorList() = errorList
    fun getHasInternetConnection(): LiveData<Int> = hasInternetConnection

    fun loginUser(user: User)  = viewModelScope.launch {
        try {
            val response = userRepository.login(user)
            if (response.isSuccessful) {
                val token = response.body()!!.token
                val message = response.body()!!.message
                val id = response.body()!!.user.id
                if (token == null ) {
                    errorList.postValue(message)
                    Log.d(TAG, "onResponse noted logged in:- ${response.body()!!.message}")
                } else if (id != null) {
                    Log.d(TAG, "loginUser: $token")
                    userInfo.postValue("$id-$token")
                }
            } else {
                Log.d(TAG, "onResponse error ${response.code()}")
            }
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, "getUser: ${ex.stackTraceToString()}")
            hasInternetConnection.postValue(MainActivity.NO_INTERNET)
        }
        catch (ex: Exception) {
        }

    }

    fun addUser(user: User) = viewModelScope.launch {
        try {
            val response = userRepository.addUser(user)
            if (response.isSuccessful) {
                val token = response.body()!!.token
                val message = response.body()!!.message
                val id = response.body()!!.user.id
                if (token == null ) {
                    errorList.postValue(message)
                } else if (id != null) {
                    userInfo.postValue("$id-$token")
                }
            }
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, "getUser: ${ex.stackTraceToString()}")
            hasInternetConnection.postValue(MainActivity.NO_INTERNET)
        }
        catch (ex: Exception) {
        }
    }

}