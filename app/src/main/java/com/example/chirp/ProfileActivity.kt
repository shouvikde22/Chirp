package com.example.chirp

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ActionCodeUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var dialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val saveBtn : Button = findViewById(R.id.editProfileButton)
        val firstName : TextView = findViewById(R.id.firs)
        saveBtn.setOnClickListener {

            showProgressBar()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.tostring()
            val bio = binding.etBio.text.toString()

            val user = User(firstName, lastName,bio)
            if (uid != null){

                databaseReference.child(uid).setValue(user).addOnCanceledListener {

                    if (it.isSuccessful){

                        uploadProfilePic()

                    }else{

                        hideProgressBar()

                    }
                }

            }

        }

        private fun uploadProfilePic() {
            imageUri = Uri.parse("android.resource://$packageName/${R.drawable.profile}")
            storageReference = FirebaseStorage.getInstance().getReference("Users/"+auth.currentUser?.uid)
            storageReference.putFile(imageUri).addOnSuccessListener {
                hideProgressBar()
                Toast.makeText(this@ProfileActivity,"Profile successfuly updated",Toast.LENGTH_SHORT).show()

            }.addOnCanceledListener {
                hideProgressBar()
                Toast.makeText(this@ProfileActivity,"Failed to upload the image",Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this@ProfileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun hideProgressBar(){
        dialog.dismiss()
    }
}

