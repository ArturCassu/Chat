package com.example.chat.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chat.R
import com.example.chat.databinding.ActivitySignInBinding
import com.example.chat.databinding.ActivitySignUpBinding
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var encodedImage: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener { onBackPressed() }
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
