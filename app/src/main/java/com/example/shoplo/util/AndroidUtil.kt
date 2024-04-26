package com.example.shoplo.util



import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.shoplo.data.UserModel

object AndroidUtil {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun passUserModelAsIntent(intent: Intent, model: UserModel) {
        intent.putExtra("username", model.username)
        intent.putExtra("phone", model.phone)
        intent.putExtra("userId", model.id)
    }

    fun getUserModelFromIntent(intent: Intent): UserModel {
        val userModel = UserModel()
        userModel.username = intent.getStringExtra("username")
        userModel.phone = intent.getStringExtra("phone")
        userModel.id = intent.getStringExtra("userId")
        return userModel
    }
}
