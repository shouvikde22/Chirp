package com.example.chirp

data class User(
    val id: String? = null,
    var name: String? = null,
    val email: String?,
    val password: String? = null,
    var picture: String? = null,
    var bio: String? = null)