package com.example.chirp

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbReference: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name") ?: ""
        val receiverUid = intent.getStringExtra("uid") ?: ""
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        mDbReference = FirebaseDatabase.getInstance().reference

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        val message = Message("shouik bromther", "me")
        messageList.add(message)
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
//                    chatRecyclerView.smoothScrollToPosition(list.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                }
            })

        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(messageText, senderUid)
                sendMessage(messageObject)
            }
        }
    }

    private fun sendMessage(messageObject: Message) {
        mDbReference.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbReference.child("chats").child(receiverRoom!!)
                    .child("messages").push().setValue(messageObject).addOnSuccessListener {
                        messageBox.setText("")
                    }
            }
    }
}




