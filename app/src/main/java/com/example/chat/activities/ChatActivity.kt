package com.example.chat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chat.databinding.ActivityChatBinding
import com.example.chat.models.User
import com.example.chat.utils.Constants

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListners()
        loadReceiverDetails()

    }

    private fun loadReceiverDetails() = with(binding){
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER, ) as User
        textName.text = receiverUser.name
    }

    private fun setListners() = with(binding){
        imageBack.setOnClickListener{ onBackPressedDispatcher.onBackPressed() }

    }

}