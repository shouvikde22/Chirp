package com.example.chirp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot

class UserViewModel: ViewModel() {

    fun convertToUserModel(document: DataSnapshot): User {

        val keys = document.children.map { it.key }
        val values = document.children.map { it.getValue(Any::class.java) }
        val user = document.getValue(User::class.java)
        Log.d("user", "$user")
        return user!!
//        val id = values
//        val name = document.getString("name")
//        val email = document.id
//        val picture = document.getString("picture") ?: ""
//        val bio = document.getString("bio") ?: ""
//
//
//        return User(id = id, name= name, email = email, picture =  picture, bio =  bio)
    }
}