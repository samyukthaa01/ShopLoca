package com.example.shoplo.activities



import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.adapters.ChatRecyclerAdapter
import com.example.shoplo.data.ChatMessageModel
import com.example.shoplo.data.ChatroomModel
import com.example.shoplo.data.UserModel
import com.example.shoplo.util.AndroidUtil
import com.example.shoplo.util.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatroomId: String
    private lateinit var chatroomModel: ChatroomModel
    private lateinit var otherUser: UserModel
    private lateinit var messageInput: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var otherUsername: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sellerId = intent.getStringExtra("sellerId")
        otherUser = AndroidUtil.getUserModelFromIntent(intent)
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId() ?: "", sellerId ?: "")


        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)

        backBtn.setOnClickListener { finish() }
        otherUsername.text = otherUser.username

        sendMessageBtn.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) sendMessageToUser(message)
        }

        setupChatRecyclerView()
        getOrCreateChatroomModel()
    }

    private fun setupChatRecyclerView() {
        val query: Query = FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .build()

        adapter = ChatRecyclerAdapter(options, applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this).apply { reverseLayout = true }
        recyclerView.adapter = adapter
        adapter.startListening()

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessageToUser(message: String) {
        chatroomModel.lastMessageTimestamp = Timestamp.now()
        chatroomModel.lastMessageSenderId = FirebaseUtil.currentUserId()!!
        chatroomModel.lastMessage = message

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)

        val chatMessageModel = ChatMessageModel(message, FirebaseUtil.currentUserId()!!, Timestamp.now())
        FirebaseUtil.getChatroomMessageReference(chatroomId)
            .add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) messageInput.setText("")
            }
    }

    private fun getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    chatroomModel = task.result.toObject(ChatroomModel::class.java)
                        ?: ChatroomModel(
                            chatroomId,
                            listOf(FirebaseUtil.currentUserId() ?: "", otherUser.id ?: ""),
                            Timestamp.now(),
                            ""
                        )

                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                }
            }
    }
}
