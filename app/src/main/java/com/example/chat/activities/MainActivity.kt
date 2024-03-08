package com.example.chat.activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.example.chat.R
import com.example.chat.databinding.ActivityMainBinding
import com.example.chat.utils.Constants
import com.example.chat.utils.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val constants = Constants()
    private lateinit var bindind: ActivityMainBinding;
    private lateinit var preferenceManager: PreferenceManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindind.root)
        preferenceManager = PreferenceManager(applicationContext)

        loadUserDetails()
        getToken()
        setListeners()
    }

    private fun setListeners(){
        bindind.imageSignOut.setOnClickListener { signOut() }
    }

    private fun loadUserDetails(){
        bindind.textName.text = preferenceManager.getString(constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        bindind.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { updateToken(it) }
    }

    private fun updateToken(token: String){
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(constants.KEY_USER_ID)!!)
        documentReference.update(constants.KEY_FCM_TOKEN, token)
//            .addOnSuccessListener { showToast("Token update successfully") }
//            .addOnFailureListener { showToast("Unable to update token") }

    }

    private fun signOut() {
        showToast("Signing out...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = preferenceManager.getString(constants.KEY_USER_ID)?.let {
            database.collection(constants.KEY_COLLECTION_USERS).document(it)
        }
        val updates = hashMapOf<String, Any>( constants.KEY_FCM_TOKEN to FieldValue.delete() )
        documentReference?.update(updates)
            ?.addOnSuccessListener {
            preferenceManager.clear()
            startActivity(Intent(applicationContext,SignInActivity::class.java))
            finish()
        }
            ?.addOnFailureListener { showToast("Unable to sign out") }


    }
}