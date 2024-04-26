package com.example.shoplo.fragments.Register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import androidx.navigation.fragment.findNavController
import com.example.shoplo.R
import com.example.shoplo.activities.ManagingActivity
import com.example.shoplo.data.Seller
import com.example.shoplo.data.Shop
import com.example.shoplo.databinding.FragmentRegisterbBinding
import com.example.shoplo.util.RegisterValidation
import com.example.shoplo.util.Resource
import com.example.shoplo.viewmodel.RegisterSharedViewModel
import com.example.shoplo.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterbFragment : Fragment(R.layout.fragment_registerb) {

    private lateinit var binding: FragmentRegisterbBinding
    private val sharedViewModel: RegisterSharedViewModel by activityViewModels()
    private val viewModel by viewModels<RegisterViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterbBinding.inflate(inflater)
        return binding.root
    }

    private fun openGoogleMaps() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode("No.157, Jalan Batu Geliga"))
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(requireContext(), "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the seller data in the sharedViewModel
        sharedViewModel.shopData.observe(viewLifecycleOwner, { shop ->
            // Handle the updated seller data
           Log.d(TAG, "Observed Shop Data: $shop")
            // You can use the seller data here or store it in a class variable for later use

            // Set the UI elements with the shop data

        })
        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = Seller(
                    shopName = edShopname.text.toString().trim(),
                    address = edPickupaddress.text.toString().trim(),
                    email = edEmail.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                if (sharedViewModel.shopData.value != null) {
                    Log.d(TAG, "User data: $user")
                    viewModel.createAccountWithEmailAndPassword(user, sharedViewModel.shopData.value!!, password)
                    // After registration, navigate back to the login page
                    findNavController().navigate(R.id.action_registerbFragment_to_loginFragment)

                } else {
                    Log.e(TAG, "User data is null") // Add this log to check if user data is null
                }
            }

        }

        binding.ivMapIcon.setOnClickListener {
            // Call the openGoogleMaps method when the map icon is clicked
            openGoogleMaps()
        }
        lifecycleScope.launch {

            // Suspend until you are STARTED
            lifecycleScope.launch { withStarted { } }

            // Run your code that happens after you become STARTED here
            viewModel.register.collect { sellerResource ->
                when (sellerResource) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        // Seller registration success, now observe shop registration
                        viewModel.shopRegister.collect { shopResource ->
                            when (shopResource) {
                                is Resource.Loading -> {
                                    binding.buttonRegisterRegister.startAnimation()
                                    // Handle shop registration loading state
                                }
                                is Resource.Success -> {
                                    // Both seller and shop registration were successful
                                    binding.buttonRegisterRegister.revertAnimation()
                                    Intent(requireActivity(), ManagingActivity::class.java).also { intent ->
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                                }
                                is Resource.Error -> {
                                    // Handle shop registration error state
                                    Log.e(TAG, shopResource.message.toString())
                                    binding.buttonRegisterRegister.revertAnimation()
                                }
                                else -> Unit
                            }
                        }
                    }
                    is Resource.Error -> {
                        // Handle seller registration error state
                        Log.e(TAG, sellerResource.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launch {
            lifecycleScope.launch { withStarted { } }
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}
