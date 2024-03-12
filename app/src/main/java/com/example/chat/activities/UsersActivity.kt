package com.example.chat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chat.R
import com.example.chat.adapters.UsersAdapter
import com.example.chat.databinding.ActivityUsersBinding
import com.example.chat.models.User
import com.example.chat.utils.Constants
import com.example.chat.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)
        setListeners()
        getUsers()
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getUsers(){
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener {
                loading(false)
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (it.isSuccessful && it.result != null){
                    val users = mutableListOf<User>()
                    for(queryDocumentSnapshot in it.result){
                        if(currentUserId.equals(queryDocumentSnapshot.id)){
                            continue
                        }
                        val user = User(
                            email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)!!,
                            name = queryDocumentSnapshot.getString(Constants.KEY_NAME)!!,
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)!!,
                            token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                        )
                        users.add(user)
                    }
                    if (users.size > 0){
                        val usersAdapter = UsersAdapter(users)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    }else{
                        showErrorMessage()
                    }
                }else{
                    showErrorMessage()
                }
            }
    }

    private fun showErrorMessage(){
        binding.textErrorMessage.text = String.format("%s", "No users available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}

