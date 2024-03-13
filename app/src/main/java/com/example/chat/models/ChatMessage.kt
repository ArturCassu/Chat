package com.example.chat.models

data class ChatMessage(
    val senderId: String,
    val receiverId: String,
    val message: String,
    val dateTime: String
)
