package com.example.chat.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import com.example.chat.adapters.ChatAdapter
import com.example.chat.databinding.ActivityChatBinding
import com.example.chat.models.ChatMessage
import com.example.chat.models.User
import com.example.chat.utils.Constants
import com.example.chat.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: List<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListners()
        loadReceiverDetails()
        init()

    }

    private fun sendMessage(){
        val message = hashMapOf(
            Constants.KEY_SENDER_ID to preferenceManager.getString(Constants.KEY_USER_ID),
            Constants.KEY_RECEIVER_ID to preferenceManager.getString(Constants.KEY_RECEIVER_ID),
            Constants.KEY_MESSAGE to binding.inputMessage.text.toString(),
            Constants.KEY_TIMESTAMP to Date(),
        )
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        binding.inputMessage.text = null
    }

    fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = emptyList<ChatMessage>().toMutableList()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedString(receiverUser.image),
            preferenceManager.getString(Constants.KEY_USER_ID)!!
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap{
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun loadReceiverDetails() = with(binding){
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER, ) as User
        textName.text = receiverUser.name
    }

    private fun setListners() = with(binding){
        imageBack.setOnClickListener{ onBackPressedDispatcher.onBackPressed() }
        layoutSend.setOnClickListener{ sendMessage() }
    }

}