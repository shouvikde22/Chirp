package com.example.chirp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chirp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.internal.RecaptchaActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class MainActivity : AppCompatActivity(){

    private lateinit var userRecyclerView: RecyclerView
    private var userList = ArrayList<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        binding.toolbarMainContainer.imgUser.setOnClickListener{
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }

        val sharedPreferences: SharedPreferences = this.getSharedPreferences("User", Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("user", null)
        val user = userDataJson?.let { Gson().fromJson(it, User::class.java) }

        Glide.with(this)
            .load(user?.picture)
            .placeholder(R.drawable.img_user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)// Add a placeholder drawable
            .error(R.drawable.img_user) // Add a drawable for error cases
            .into(binding.toolbarMainContainer.imgUser)

        binding.toolbarMainContainer.textPageTittle.text = getString(R.string.chats)

        userList = ArrayList<User>()
        adapter = UserAdapter(this)
        adapter.saveData(userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.child("User").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children){
                    Log.d("User", postSnapshot.value.toString())
                    val currentUser = User(
                        id = postSnapshot?.key,
                        name = postSnapshot?.child("name")?.getValue(String::class.java),
                        email = postSnapshot?.child("email")?.getValue(String::class.java),
                        picture = postSnapshot?.child("picture")?.getValue(String::class.java)
                    )
                    Log.d("current","${user?.email}    ${currentUser.id.toString()}")
                    if(user?.email  != currentUser.email.toString()){
                        userList.add(currentUser)
                    }
            }

                Log.d("user list","$userList")
                adapter.saveData(userList)
                userRecyclerView.scrollToPosition(userList.size - 1)
        }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
     override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.menu,menu)
         return super.onCreateOptionsMenu(menu)
     }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){
            //write the login for the logout
            mAuth.signOut()
            val intent = Intent(this@MainActivity,RecaptchaActivity::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}