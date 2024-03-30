package com.example.shoplo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        // Assuming you have a different mechanism to add orders to the "orders" collection
        // Add the order into the "orders" collection
        firestore.collection("orders")
            .document()
            .set(order)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _order.emit(Resource.Success(listOf(order)))
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _order.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    // Function to retrieve orders from the "orders" collection
    fun getOrders() {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        firestore.collection("orders").get()
            .addOnSuccessListener { result ->
                val ordersList = mutableListOf<Order>()
                for (document in result) {
                    val order = document.toObject(Order::class.java)
                    ordersList.add(order)
                }

                viewModelScope.launch {
                    _order.emit(Resource.Success(ordersList))
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _order.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
}
