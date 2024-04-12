package com.example.shoplo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.data.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val postList: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPost: ImageView = itemView.findViewById(R.id.Img)
        val textViewPostTitle: TextView = itemView.findViewById(R.id.pname)
        // Add more views here as per your post design
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]

        // Assuming postImages is a list of image URLs in your Post class
        val imageUrl = post.postImages.firstOrNull() // You may need to handle multiple images here

        // Use Picasso to load the image into the ImageView
        if (imageUrl != null) {
            Picasso.get()
                .load(imageUrl)
                .error(R.drawable.icon_background) // Set a placeholder for image load errors
                .into(holder.imageViewPost)
        } else {
            // Handle the case where there is no image URL
            // You may want to set a placeholder image or handle this case differently
            holder.imageViewPost.setImageResource(R.drawable.icon_background)
        }
        // Set post title to the TextView
        holder.textViewPostTitle.text = post.postTitle

        // Add more data binding here as per your post design

        holder.itemView.setOnClickListener {
            onClick?.invoke(post)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
    var onClick: ((Post) -> Unit)? = null

    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }
}
