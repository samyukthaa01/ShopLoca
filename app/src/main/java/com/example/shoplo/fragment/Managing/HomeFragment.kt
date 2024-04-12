package com.example.shoplo.fragment.Managing

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shoplo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the BottomNavigationView by ID
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Set click listener for the profile icon in the bottom menu
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.userAccountFragment -> {
                    // Navigate to the UserAccountFragment when the profile icon is clicked
                    findNavController().navigate(R.id.action_homeFragment_to_userAccountFragment)
                    true
                }
                else -> false
            }
        }

        // Find the ImageView by ID
        val productImage = view.findViewById<ImageView>(R.id.productImage)

        // Set click listener for the productImage ImageView
        productImage.setOnClickListener {
            onProductImageClick(it)
        }

        // Find the ImageView by ID
        val faqImage = view.findViewById<ImageView>(R.id.roundin8)

        // Set click listener for the faqImage ImageView
        faqImage.setOnClickListener {
            onFaqImageClick(it)
        }

        val guideImage = view.findViewById<ImageView>(R.id.roundin7)

        // Set click listener for the faqImage ImageView
        guideImage.setOnClickListener {
            onGuideImageClick(it)
        }

        val contentImage = view.findViewById<ImageView>(R.id.roundin2)

        // Set click listener for the faqImage ImageView
        contentImage.setOnClickListener {
            onContentImageClick(it)
        }

        // Find the Button by ID (Replace with the actual ID of your button)
        val orderButton = view.findViewById<ImageView>(R.id.roundin3)

        // Set click listener for the orderButton
        orderButton.setOnClickListener {
            onOrderClick(it)
        }
    }

    private fun onGuideImageClick(view: View) {
        findNavController().navigate(R.id.action_homeFragment_to_guideFragment)
    }

    // New method for handling Order button click
    fun onOrderClick(view: View) {
        // Handle the click event here
        // For example, you can navigate to the Order Fragment
        findNavController().navigate(R.id.action_homeFragment_to_allOrdersFragment)
    }

    fun onFaqImageClick(view: View) {
        // Handle the click event here
        // For example, you can navigate to the FAQ fragment
        findNavController().navigate(R.id.action_homeFragment_to_faqFragment)
    }

    fun onContentImageClick(view: View) {
        // Handle the click event here
        // For example, you can navigate to the Order Fragment
        findNavController().navigate(R.id.action_homeFragment_to_postFragment)
    }
    // Move the function definition outside onViewCreated
    fun onProductImageClick(view: View) {
        // Handle the click event here
        // For example, you can navigate to another fragment
        findNavController().navigate(R.id.action_homeFragment_to_productdisplayFragment)
    }
}
