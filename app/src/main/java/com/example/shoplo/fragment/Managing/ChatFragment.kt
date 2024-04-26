package com.example.shoplo.fragment.Managing



import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.activities.SearchUserActivity
import com.example.shoplo.adapters.RecentChatRecyclerAdapter
import com.example.shoplo.data.ChatroomModel
import com.example.shoplo.util.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {

    // Chat Page
    private lateinit var searchButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecentChatRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        setupRecyclerView()

        searchButton = view.findViewById(R.id.main_search_btn)
        searchButton.setOnClickListener {
            // Use requireActivity() to get the activity context
            startActivity(Intent(requireActivity(), SearchUserActivity::class.java))
        }

        return view
    }

    private fun setupRecyclerView() {
        val query = FirebaseUtil.allChatroomCollectionReference()
            .whereArrayContains("userIds", FirebaseUtil.currentUserId() ?: "")
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)


        val options = FirestoreRecyclerOptions.Builder<ChatroomModel>()
            .setQuery(query, ChatroomModel::class.java).build()

        adapter = RecentChatRecyclerAdapter(options, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
