package com.example.shoplo.adapters



import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoplo.R
import com.example.shoplo.activities.ChatActivity
import com.example.shoplo.data.ChatroomModel
import com.example.shoplo.data.UserModel
import com.example.shoplo.util.AndroidUtil
import com.example.shoplo.util.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

/**
 * Adapter class for populating RecyclerView with recent chatroom data.
 * Responsible for binding chatroom data to ViewHolder and handling user interaction.
 */
class RecentChatRecyclerAdapter(options: FirestoreRecyclerOptions<ChatroomModel>, private val context: Context) :
    FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>(options) {

    companion object {
        private const val TAG = "RecentChatRecyclerAdapter"
    }

    /**
     * Binds chatroom data to the ViewHolder.
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item within the adapter's data set.
     * @param model The chatroom model containing data to be bound.
     */
    override fun onBindViewHolder(holder: ChatroomModelViewHolder, position: Int, model: ChatroomModel) {
        if (!model.userIds.isNullOrEmpty()) {
            // Access userIds safely
            val userIds = model.userIds
            FirebaseUtil.getOtherUserFromChatroom(userIds!!)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Access lastMessageTimestamp safely
                        val lastMessageTimestamp = model.lastMessageTimestamp
                        val lastMessageSentByMe = model.lastMessageSenderId == FirebaseUtil.currentUserId()
                        val otherUserModel = task.result?.toObject(UserModel::class.java)
                        holder.usernameText.text = otherUserModel?.username
                        holder.lastMessageText.text = if (lastMessageSentByMe) {
                            "You: ${model.lastMessage}"
                        } else {
                            model.lastMessage
                        }
                        // Check for nullability of lastMessageTimestamp
                        lastMessageTimestamp?.let { timestamp ->
                            holder.lastMessageTime.text = FirebaseUtil.timestampToString(timestamp)
                        }

                        // Load profileImg using Glide
                        otherUserModel?.profileImg?.let { profileImg ->
                            if (profileImg.isNotEmpty()) {
                                Glide.with(context)
                                    .load(profileImg)
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.profilePic)
                            }
                        }

                        holder.itemView.setOnClickListener {
                            // Navigate to chat activity
                            val intent = Intent(context, ChatActivity::class.java)
                            if (otherUserModel != null) {
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel)
                            }
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }
                    }
                }
        } else {
            Log.e(TAG, "Model or user IDs are null")
        }

    }

    /**
     * Inflates the layout for the ViewHolder.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false)
        return ChatroomModelViewHolder(view)
    }

    /**
     * ViewHolder class for holding chatroom item views.
     */
    inner class ChatroomModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameText: TextView = itemView.findViewById(R.id.user_name_text)
        val lastMessageText: TextView = itemView.findViewById(R.id.last_message_text)
        val lastMessageTime: TextView = itemView.findViewById(R.id.last_message_time_text)
        val profilePic: ImageView = itemView.findViewById(R.id.profile_pic_image_view)
    }
}
