package com.example.chirp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chirp.databinding.ActivityProfileBinding
import com.example.fashionadvisor.com.example.chirp.CheckNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.internal.RecaptchaActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var storageRef : StorageReference
    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences("User", Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("user", null)
        val user = userDataJson?.let { Gson().fromJson(it, User::class.java)}

        storageRef = FirebaseStorage.getInstance().getReference("Images")
        dbRef = FirebaseDatabase.getInstance().getReference("User").child(user?.id.toString())

        Log.d("Saved user", user.toString())
        if (user != null) {
            if (user.picture != null){
                Log.d("Saved User", "$user")
                Glide.with(this)
                    .load(user.picture)
                    .placeholder(R.drawable.img_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)// Add a placeholder drawable
                    .error(R.drawable.img_user) // Add a drawable for error cases
                    .into(binding.imgUserProfile)
            }
            binding.editTextName.setText(user.name)
            if (user.picture != null){
                binding.editTextBio.setText(user.bio)
            }
        }


        binding.buttonSaveProfile.setOnClickListener{
            if(CheckNetwork.isInternetAvailable(this)) {
                if (binding.editTextName.visibility == View.VISIBLE) {
                    if (user != null) {
                        user.name = binding.editTextName.text.toString()
                        user.bio = binding.editTextBio.text.toString()
                        dbRef.updateChildren(mapOf("name" to user.name, "bio" to user.bio))
                            .addOnCompleteListener {
                                Log.d("dbref", "success")
                                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                                StoreUser.saveData(user, this)
                            }
                            .addOnFailureListener {
                                Log.d("dbref", "failed")
                            }
                    }
                }
            }else{
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            }
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.imgUserProfile.setImageURI(uri)
            Log.d("picker", "success")

            if (uri != null) {
                storageRef.child(user!!.email.toString()).putFile(uri)
                    .addOnSuccessListener { task ->
                        Log.d("storage", "success")
                        Log.d("email", "$user")

                        // Retrieve the download URL of the uploaded image
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                val img = url.toString()
                                user.picture = img
                                StoreUser.saveData(user, this)
                                // Update the user's document in Firestore with the image URL
                                dbRef.updateChildren(mapOf("picture" to img))
                                    .addOnCompleteListener {
                                        Log.d("dbref", "success")
                                        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Log.d("dbref", "failed")
                                    }
                            }
                    }
                    .addOnFailureListener {
                        Log.e("storage", "failed")
                    }
            }
        }


        binding.icCam.setOnClickListener{
            if(CheckNetwork.isInternetAvailable(this)){
                pickImage.launch("image/*")
            }else{
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            sharedPreferences.edit().clear().apply()
            Toast.makeText(this, "Loged Out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,RecaptchaActivity::class.java))
            finish()

        }

        binding.imgButtonBack.setOnClickListener{
            super.onBackPressed()
        }
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        Intent()
        return super.getOnBackInvokedDispatcher()
    }

}