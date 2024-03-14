package com.example.chat.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.example.chat.adapters.ChatAdapter
import com.example.chat.databinding.ActivityChatBinding
import com.example.chat.models.ChatMessage
import com.example.chat.models.User
import com.example.chat.utils.Constants
import com.example.chat.utils.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: MutableList<ChatMessage>
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
        listenMessages()

    }

    private fun listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener  = EventListener<QuerySnapshot>{ value: QuerySnapshot?, error:Throwable? ->
        if (error != null){

        }
        if(value != null){
            val count = chatMessages.size
            for (document in value.documentChanges){
                if(document.type == DocumentChange.Type.ADDED){
                    val chatMessage = ChatMessage(
                        document.document.getString(Constants.KEY_SENDER_ID)!!,
                        document.document.getString(Constants.KEY_RECEIVER_ID)!!,
                        document.document.getString(Constants.KEY_MESSAGE)!!,
                        getReadableDateTime(document.document.getDate(Constants.KEY_TIMESTAMP)!!),
                        document.document.getDate(Constants.KEY_TIMESTAMP)!!
                    )
                    chatMessages.add(chatMessage)
                }
            }
            chatMessages.sortWith(Comparator { obj1:ChatMessage, obj2:ChatMessage -> obj1.dateObject.compareTo(obj2.dateObject) })
            if (count == 0){
                chatAdapter.notifyDataSetChanged()
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
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

    private fun getReadableDateTime(date: Date): String{
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

}