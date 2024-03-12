package com.example.chat.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chat.databinding.ActivitySignUpBinding
import com.example.chat.utils.Constants
import com.example.chat.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)

        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.buttonSignUp.setOnClickListener{
            if (isValidSignUpDetails()){
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun signUp(){
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user = hashMapOf<String, Any>(
                Constants.KEY_NAME to binding.inputName.text.toString() ,
                Constants.KEY_EMAIL to binding.inputEmail.text.toString() ,
                Constants.KEY_PASSWORD to binding.inputPassword.text.toString(),
                Constants.KEY_IMAGE to encodedImage!!
            )
        database.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener {
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED,true)
                preferenceManager.putString(Constants.KEY_USER_ID, it.id)
                preferenceManager.putString(Constants.KEY_NAME, binding.inputName.text.toString())
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage!!)
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e->
                loading(false)
                e.message?.let { message -> showToast(message) }
            }
    }

    private fun encodeImage(bitmap: Bitmap):String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

   private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
   {
       if (it.resultCode == RESULT_OK) {
           if (it.data != null){
               val imageUri = it.data?.data
               try {
                   val inputStream = imageUri?.let { uri -> contentResolver.openInputStream(uri) }
                   val bitmap = BitmapFactory.decodeStream(inputStream)
                   binding.imageProfile.setImageBitmap(bitmap)
                   binding.textAddImage.visibility = View.INVISIBLE
                   encodedImage = encodeImage(bitmap)
               }catch (e: FileNotFoundException){
                   e.printStackTrace()
               }
           }
       }
   }
    private fun isValidSignUpDetails():Boolean{
        if (encodedImage == null){
            showToast("Select profile image")
            return false
        }else if (binding.inputName.text.toString().trim().isEmpty()){
            showToast("Enter Name")
            return false
        }else if (binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter Email")
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Enter valid email")
            return false
        }else if (binding.inputPassword.text.toString().isEmpty()) {
            showToast("Enter Password")
            return false
        }else if (binding.inputConfirmPassword.text.toString() != binding.inputPassword.text.toString()) {
            showToast("Passwords don't match")
            return false
        }else{
            return true
        }
    }

    private fun loading(isLoading: Boolean){
        if (isLoading){
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.buttonSignUp.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
