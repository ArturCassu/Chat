package com.example.chat.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.databinding.ItemContainerUserBinding
import com.example.chat.models.User

class UsersAdapter(private val users: List<User>): RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
    class UserViewHolder(itemContainerUserBinding: ItemContainerUserBinding, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = itemContainerUserBinding

        fun setUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            binding.imageProfile.setImageBitmap(getUserImage(user.image))
        }
        private fun getUserImage(encodedString: String): Bitmap {
            val bytes = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainerUserBinding = ItemContainerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemContainerUserBinding, parent)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }
}