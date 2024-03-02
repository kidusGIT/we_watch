package com.example.wewatchmvvm.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvvm.model.User
import com.example.wewatchmvvm.repository.data.UserApiCall
import com.example.wewatchmvvm.repository.repository.UserRepository
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class MeFragmentViewModel(userApiCall: UserApiCall): ViewModel() {
    private val TAG = "MeFragmentViewModel"
    private var userRepository: UserRepository = UserRepository(userApiCall)

    private val user = MutableLiveData<User>()
    private val isEdited = MutableLiveData<Boolean>()
    private val hasInternetConnection = MutableLiveData<Int>()

    fun getUserData(): LiveData<User> = user
    fun getIsEdited(): LiveData<Boolean> = isEdited

    fun getHasInternetConnection(): LiveData<Int> = hasInternetConnection

    fun getUser(token: String) = viewModelScope.launch {
        try {
            val res = userRepository.getUser(token)
            if (res.isSuccessful) {
                user.postValue(res.body())
            }
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, "getUser: ${ex.stackTraceToString()}")
            hasInternetConnection.postValue(1)
        }
        catch (ex: Exception) {
        }
    }

    fun editUser(token: String, user: User) = viewModelScope.launch {
        val res = userRepository.editUser(token, user)
        if (res.isSuccessful) {
            isEdited.postValue(true)
        } else {
            isEdited.postValue(false)
        }
        Log.d(TAG, "editUser: ${token}")
    }

}