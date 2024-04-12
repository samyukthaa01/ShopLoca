package com.example.shoplo.fragment.Managing

import com.example.shoplo.data.Post
import com.example.shoplo.adapters.PostAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class PostFragment : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private val postList: MutableList<Post> = mutableListOf()
    private val firestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.postRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Create and set up the adapter
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        // In PostDisplayFragment
        postAdapter.onClick = { selectedPost ->
            val action = PostFragmentDirections
                .actionPostFragmentToPostDetailsFragment(selectedPost)
            findNavController().navigate(action)
        }

        // Fetch posts from Firestore
        fetchPosts()

        // Add Post FloatingActionButton
        val fabAddPost: FloatingActionButton = view.findViewById(R.id.fabAddPost)
        fabAddPost.setOnClickListener {
            // Navigate to the post details fragment to add a new post
            findNavController().navigate(R.id.action_postFragment_to_postDetailsFragment)
        }

        return view
    }

    private fun fetchPosts() {
        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Get the userID
        val sellerID = currentUser?.uid ?: ""

        firestore.collection("Posts")
            .whereEqualTo("sellerID", sellerID)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val newPosts = documents.map { it.toObject(Post::class.java) }
                    postAdapter.updatePosts(newPosts)
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("Firestore", "Error getting posts", exception)
            }
    }
}
