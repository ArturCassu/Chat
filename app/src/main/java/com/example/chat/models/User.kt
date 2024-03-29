package com.example.chat.models

import java.io.Serializable

data class User(
    var name: String,
    var image: String,
    var email: String,
    var token: String?,
    var id: String
): Serializable
