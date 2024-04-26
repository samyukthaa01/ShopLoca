package com.example.shoplo.activities



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.adapters.SearchUserRecyclerAdapter
import com.example.shoplo.data.UserModel
import com.example.shoplo.util.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import android.widget.EditText
import android.widget.ImageButton

class SearchUserActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: SearchUserRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        searchInput = findViewById(R.id.search_username_input)
        searchButton = findViewById(R.id.search_user_btn)
        backButton = findViewById(R.id.back_btn)
        recyclerView = findViewById(R.id.search_user_recycler_view)

        searchInput.requestFocus()

        backButton.setOnClickListener {
            onBackPressed()
        }

        searchButton.setOnClickListener {
            val searchTerm = searchInput.text.toString()
            if (searchTerm.isEmpty() || searchTerm.length < 3) {
                searchInput.error = "Invalid Username"
                return@setOnClickListener
            }
            setupSearchRecyclerView(searchTerm)
        }
    }

    private fun setupSearchRecyclerView(searchTerm: String) {
        val query = FirebaseUtil.allUserCollectionReference()
            .whereGreaterThanOrEqualTo("username", searchTerm)

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java).build()

        adapter = SearchUserRecyclerAdapter(options, applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::adapter.isInitialized) {
            adapter.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }

}
