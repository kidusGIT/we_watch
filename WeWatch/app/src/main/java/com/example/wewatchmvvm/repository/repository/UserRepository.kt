package com.example.wewatchmvvm.repository.repository

import com.example.wewatchmvvm.model.User
import com.example.wewatchmvvm.repository.data.UserApiCall

class UserRepository(private val api: UserApiCall) {

//    login a user
    suspend fun login(user: User) = api.login(
        username = user.username,
        password = user.password
    )

    suspend fun editUser(token: String, user: User) = api.editUser(
        token = token,
        username = user.username,
        fullName = user.full_name!!,
        bio = user.bio
    )

    suspend fun getUser(token: String) = api.getUser(token)

    suspend fun addUser(user: User) = api.addUser(
        username = user.username,
        password = user.password,
        bio = user.bio,
        fullName = user.full_name!!
    )

}