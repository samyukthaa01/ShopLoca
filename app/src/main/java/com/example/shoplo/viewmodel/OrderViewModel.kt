package com.example.shoplo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoplo.data.Item
import com.example.shoplo.data.Order
import com.example.shoplo.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    // Function to retrieve orders and their items from the "Order" collection
    // Function to retrieve orders and their items from the "Order" collection
    fun getOrders() {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        firestore.collection("Order").get()
            .addOnSuccessListener { result ->
                val ordersList = mutableListOf<Order>()
                for (document in result) {
                    val order = document.toObject(Order::class.java)

                    // Get the items for this order
                    document.reference.collection("Items").get()
                        .addOnSuccessListener { itemsResult ->
                            val itemsList = mutableListOf<Item>() // Replace with your Item class
                            for (itemDocument in itemsResult) {
                                val item = itemDocument.toObject(Item::class.java)
                                itemsList.add(item)
                            }

                            // Add the items to the order
                            order.items = itemsList

                            // Add the order to the orders list
                            ordersList.add(order)
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _order.emit(Resource.Error(exception.message.toString()))
                            }
                        }

                    viewModelScope.launch {
                        _order.emit(Resource.Success(ordersList))
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _order.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

}
