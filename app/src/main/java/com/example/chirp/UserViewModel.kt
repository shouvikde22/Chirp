package com.example.chirp

//import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot

class UserViewModel: ViewModel() {

    fun convertToUserModel(document: DataSnapshot): User {
        val id : String? = document.key
        val name: String? = document.child("name").getValue(String::class.java)
        val email: String? = document.child("email").getValue(String::class.java)
        val picture = document.child("picture").getValue(String::class.java) ?: ""
        val bio = document.child("bio").getValue(String::class.java) ?: ""

        return User(id = id, name= name, email = email, picture =  picture, bio =  bio)
    }
}