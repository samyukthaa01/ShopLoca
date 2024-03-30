package com.example.shoplo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.shoplo.data.Seller
//import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoplo.data.Shop
import javax.inject.Inject

@HiltViewModel

class RegisterSharedViewModel @Inject constructor(): ViewModel() {

    private val _shopData = MutableLiveData<Shop>()
    val shopData: LiveData<Shop> get() = _shopData

    fun setShopData(shop: Shop) {
        _shopData.value = shop
        Log.d("SharedViewModel", "Shop data set: $shop")
    }
    }

    // Add any other shared functionalities or data you need
