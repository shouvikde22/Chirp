package com.example.chirp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.example.chirp.databinding.ActivityWelcomeScreenBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binder: ActivityWelcomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setting the binder class
        binder = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binder.root)

        //setting the color of the status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        //initializing the firebase auth
        val auth = FirebaseAuth.getInstance().currentUser

        //setting the welcome page according to hte new or existing user
        val intent = if(auth != null){
            binder.btnNext.visibility = View.GONE
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, RegLoginContActivity::class.java)
        }

        Handler().postDelayed({
            if(binder.btnNext.visibility == View.GONE){
                startActivity(intent)
                finish()
            }else{
                binder.btnNext.setOnClickListener{
                    startActivity(intent)
                    finish()
                }
            }

        }, 1000)
    }
}