package com.example.shoplo.fragments.Register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shoplo.R
import com.example.shoplo.data.Seller
import com.example.shoplo.data.Shop
import com.example.shoplo.databinding.FragmentRegisteraBinding
import com.example.shoplo.viewmodel.RegisterSharedViewModel
import java.util.UUID

class RegisteraFragment : Fragment(R.layout.fragment_registera) {
    private lateinit var binding: FragmentRegisteraBinding
    private val sharedViewModel: RegisterSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisteraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener{
            findNavController().navigate(R.id.action_registeraFragment_to_loginFragment)
        }

        binding.buttonNext.setOnClickListener {
            val shop = Shop(
                binding.edFullName.text.toString().trim(),
                binding.edICnum.text.toString().trim(),
                binding.edhpnum.text.toString().trim()
            )
            sharedViewModel.setShopData(shop)
            // Log the value of sellerData in RegisteraFragment
            if (sharedViewModel.shopData.value != null) {
                Log.d("RegisteraFragment", "Shop Data in RegisteraFragment: ${sharedViewModel.shopData.value}")
                findNavController().navigate(R.id.action_registeraFragment_to_registerbFragment)
            } else {
                Log.e("RegisteraFragment", "Error: Shop Data is null")
                // Handle the case where the data couldn't be passed, e.g., show an error message or take appropriate action
            }
            //findNavController().navigate(R.id.action_registeraFragment_to_registerbFragment)
        }
    }
}

