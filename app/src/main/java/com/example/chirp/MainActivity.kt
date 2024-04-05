package com.example.chirp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.internal.RecaptchaActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(){

    private lateinit var userRecyclerView: RecyclerView
    private var userList = ArrayList<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList<User>()
        adapter = UserAdapter(this)
        adapter.saveData(userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.child("User").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    Log.d("User", postSnapshot.value.toString())
                    val currentUser = User(
                        id = postSnapshot?.child("id")?.getValue(String::class.java),
                        name = postSnapshot?.child("name")?.getValue(String::class.java),
                        email = postSnapshot?.child("email")?.getValue(String::class.java)
                    )
                    Log.d("current","${mAuth.currentUser?.uid}    ${currentUser.id.toString()}")
                    if(mAuth.currentUser?.uid  != currentUser.id.toString()){
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