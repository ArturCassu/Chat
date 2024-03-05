package com.example.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chat.R
import com.example.chat.databinding.ActivitySignInBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners(){
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener {
            addDataToFirestore()
        }
    }

    private fun addDataToFirestore(){
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val data = hashMapOf(
        "first_name" to "Artur",
        "last_name" to "Cassu"
        )
        database.collection("users")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

    }

}