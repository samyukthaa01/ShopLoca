package com.example.shoplo.fragment.Managing

import ProductAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.data.Product
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var materialCardView: MaterialCardView? = null
    private var searchView: SearchView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        materialCardView = view.findViewById(R.id.materialCardView)
        val localSearchView = view.findViewById<SearchView>(R.id.searchView)
        searchView = localSearchView

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        localSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Perform search as user types
                performSearch(newText)
                return true
            }
        })

        return view
    }

    private fun performSearch(query: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Products")
            .whereEqualTo("productName", query)
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.map { doc -> doc.toObject(Product::class.java) }
                updateRecyclerView(products)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun updateRecyclerView(products: List<Product>) {
        val adapter = ProductAdapter(products.toMutableList()).apply {
            onClick = { product ->
                navigateToProductDetailViewFragment(product)
            }
        }
        recyclerView?.adapter = adapter
    }

    private fun navigateToProductDetailViewFragment(product: Product) {
        val action = SearchFragmentDirections.actionSearchFragmentToProductDetailViewFragment(product)
        findNavController().navigate(action)
    }
}
