package com.example.wewatchmvvm.model

data class User(
    val bio: String? = null,
    val created_at: String? = null,
    val full_name: String? = null,
    val id: Int? = null,
    val password: String = "",
    val updated_at: String? = null,
    val username: String
)