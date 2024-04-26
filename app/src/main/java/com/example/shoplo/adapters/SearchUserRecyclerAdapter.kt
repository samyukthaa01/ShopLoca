package com.example.shoplo.adapters



import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoplo.R
import com.example.shoplo.activities.ChatActivity
import com.example.shoplo.data.UserModel
import com.example.shoplo.util.AndroidUtil
import com.example.shoplo.util.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SearchUserRecyclerAdapter(options: FirestoreRecyclerOptions<UserModel>, private val context: Context) :
    FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder>(options) {

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: UserModel) {
        holder.phoneText.text = model.phone
        val currentUserId = FirebaseUtil.currentUserId()
        if (currentUserId != null && currentUserId == model.id) {
            holder.usernameText.text = "${model.username}(Me)"
        } else {
            holder.usernameText.text = model.username
        }

        // Load profileImg using Glide
        if (!model.profileImg.isNullOrEmpty()) {
            Glide.with(context)
                .load(model.profileImg)
                .placeholder(R.drawable.placeholder)
                .into(holder.profilePic)
        }

        holder.itemView.setOnClickListener {
            // Navigate to chat activity
            val intent = Intent(context, ChatActivity::class.java)
            AndroidUtil.passUserModelAsIntent(intent, model)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false)
        return UserModelViewHolder(view)
    }

    inner class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameText: TextView = itemView.findViewById(R.id.user_name_text)
        val phoneText: TextView = itemView.findViewById(R.id.phone_text)
        val profilePic: ImageView = itemView.findViewById(R.id.profile_pic_image_view)
    }
}
