package com.example.chat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chat.databinding.ActivitySignInBinding
import com.example.chat.utils.Constants
import com.example.chat.utils.PreferenceManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager
    private val constants = Constants()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager.getBoolean(constants.KEY_IS_SIGNED)){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners(){
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener{
            if (isValidSignInDetails()){
                signIn()
            }
        }
    }
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun signIn(){
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(constants.KEY_COLLECTION_USERS)
            .whereEqualTo(constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(constants.KEY_PASSWORD, binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result != null && it.result.documents.size > 0){
                    val documentSnapshot = it.result.documents[0]
                    preferenceManager.putBoolean(constants.KEY_IS_SIGNED, true)
                    preferenceManager.putString(constants.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(constants.KEY_NAME, documentSnapshot.getString(constants.KEY_NAME)!!)
                    preferenceManager.putString(constants.KEY_IMAGE, documentSnapshot.getString(constants.KEY_IMAGE)!!)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                else{
                    loading(false)
                    showToast("Unable to sign in\nEmail or password incorrect")
                }
            }
    }


    private fun isValidSignInDetails():Boolean{
        if (binding.inputEmail.text.toString().trim().isEmpty()){
            showToast("Enter email")
            return false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Enter a valid email")
            return false
        }else if (binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Enter password")
            return false
        }else{
            return true
        }
    }

    private fun loading(isLoading: Boolean){
        if (isLoading){
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.buttonSignIn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }




}