package com.example.shoplo.fragment.Managing

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoplo.R
import com.example.shoplo.activities.ManagingActivity
import com.example.shoplo.adapters.ColorsAdapter
import com.example.shoplo.adapters.SizesAdapter
import com.example.shoplo.adapters.ViewPager2Images

import com.example.shoplo.data.Product
import com.example.shoplo.databinding.FragmentProductdetailviewBinding
import com.example.shoplo.util.Resource

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailViewFragment : Fragment() {
    private val args by navArgs<ProductDetailViewFragmentArgs>()
    private lateinit var binding: FragmentProductdetailviewBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val firestore = Firebase.firestore
    private var selectedColor: Int? = null
    private var selectedSize: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductdetailviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }



        binding.apply {
            tvProductName.text = product.productName
            tvProductPrice.text = "RM ${product.price}"
            tvProductDescription.text = product.productDescription

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
        }

        viewPagerAdapter.differ.submitList(product.productImages)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }


        binding.buttonDeleteProduct.setOnClickListener {
            showDeleteConfirmationDialog()
        }




    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }





    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteProduct()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteProduct() {
        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Get the sellerID
        val sellerID = currentUser?.uid ?: ""

        // Delete the product from Firestore
        firestore.collection("Products").document(sellerID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error deleting product", exception)
            }
    }


}