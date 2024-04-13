package com.example.chirp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chirp.databinding.ActivityChatBinding
import com.example.chirp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson

class ChatActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbReference: DatabaseReference
    private lateinit var binding: ActivityChatBinding

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        val name = intent.getStringExtra("name") ?: ""
        val receiverUid = intent.getStringExtra("uid") ?: ""
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val picture = intent.getStringExtra("picture") ?: ""

        mDbReference = FirebaseDatabase.getInstance().reference
        binding.toolbarChatRoom.textUsername.text = name
        Glide.with(this)
            .load(picture)
            .placeholder(R.drawable.img_user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)// Add a placeholder drawable
            .error(R.drawable.img_user) // Add a drawable for error cases
            .into(binding.toolbarChatRoom.imgUser)

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        supportActionBar?.title = name

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this)

        binding.recyclerChatRoom.layoutManager = LinearLayoutManager(this)
        binding.recyclerChatRoom.adapter = messageAdapter
//        val message = Message("shouik bromther", "me")
//        messageList.add(message)
        messageAdapter.saveData(messageList)

        mDbReference.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.let { list.add(it) }
                    }
                    messageAdapter.saveData(list)
                    if(list.size != 0) {
                        binding.recyclerChatRoom.smoothScrollToPosition(list.size - 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                }
            })

        binding.icSendMsg.setOnClickListener {
            val messageText = binding.textTypeMsg.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(messageText, senderUid)
                sendMessage(messageObject)
            }
        }
        binding.toolbarChatRoom.icBack.setOnClickListener{
            super.onBackPressed()
        }

    }

    private fun sendMessage(messageObject: Message) {
        mDbReference.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbReference.child("chats").child(receiverRoom!!)
                    .child("messages").push().setValue(messageObject).addOnSuccessListener {
                        binding.textTypeMsg.setText("")
                    }
            }
    }
}




