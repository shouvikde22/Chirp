package com.example.chirp

import android.content.Context
import com.google.gson.Gson

class StoreUser {
    companion object {
        fun saveData(user: User, context: Context) {
            val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
            val gson = Gson()
            sharedPreferences.edit().putString("user", gson.toJson(user)).apply()
        }
    }
}