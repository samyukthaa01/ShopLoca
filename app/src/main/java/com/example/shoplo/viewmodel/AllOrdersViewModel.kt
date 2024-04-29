package com.example.shoplo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoplo.data.Order
import com.example.shoplo.data.CurrentUser
import com.example.shoplo.data.Item
import com.example.shoplo.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    private val _allItemIDs = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val allItemIDs: StateFlow<Resource<List<String>>> = _allItemIDs.asStateFlow()

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Get the sellerID
        val sellerID = currentUser?.uid ?: ""

        // Create an empty list to hold the orders
        val orders = mutableListOf<Order>()

        // Get a reference to the Order collection
        val orderRef = firestore.collection("Order")

        // Log sellerID for debugging
        Log.d("AllOrdersViewModel", "Seller ID: $sellerID")

        // Fetch all documents from the Order collection
        orderRef.get().addOnSuccessListener { orderDocuments ->
            for (orderDocument in orderDocuments) {
                // For each order, fetch the Orders subcollection
                orderDocument.reference.collection("Orders").get()
                    .addOnSuccessListener { ordersDocuments ->
                        for (ordersDocument in ordersDocuments) {
                            // For each Orders document, fetch the Items subcollection
                            ordersDocument.reference.collection("Items")
                                .whereEqualTo("sellerID", sellerID)
                                .get()
                                .addOnSuccessListener { itemsDocuments ->
                                    // Log the number of items fetched for debugging
                                    Log.d("AllOrdersViewModel", "Number of items fetched: ${itemsDocuments.size()}")

                                    val items = mutableListOf<Item>()
                                    for (itemDocument in itemsDocuments) {
                                        // Create an Item object from the retrieved document
                                        val itemId = itemDocument.getString("productId") ?: ""
                                        val productName = itemDocument.getString("productName") ?: ""
                                        val productPrice = itemDocument.getString("productPrice") ?: ""
                                        val totalPrice = itemDocument.getDouble("totalPrice") ?: 0.0f
                                        val totalQuantity = itemDocument.getString("totalQuantity")?: ""
                                        val selectedColor = itemDocument.getString(("selectedColor"))?: ""
                                        val selectedSize = itemDocument.getString(("selectedSize"))?: ""
                                        val productImage = itemDocument.get("productImage")
                                        val productImageList = if (productImage is List<*>) {
                                            // If it's already a list, cast it to List<String>
                                            productImage as List<String>
                                        } else if (productImage is String) {
                                            // If it's a string, create a list with a single element
                                            listOf(productImage)
                                        } else {
                                            // Handle other cases if necessary
                                            // For example, you may want to throw an exception or provide a default value
                                            throw IllegalArgumentException("Invalid type for productImage")
                                        } // assuming productImage is a List<String>

                                        val item = Item(itemId, productImageList, productName, productPrice,selectedSize, selectedColor,totalPrice, totalQuantity)
                                        // Add the Item object to the list
                                        items.add(item)
                                    }

                                    // If any item with matching sellerID is found, include the order
                                    if (!itemsDocuments.isEmpty) {
                                        // Log success message for debugging
                                        Log.d("AllOrdersViewModel", "Items found for seller ID: $sellerID")

                                        // Convert the Orders document to Order object
                                        val order = ordersDocument.toObject(Order::class.java)
                                        order.items = items // Add the items to the order

                                        // Fetch the customer information
                                        firestore.collection("CurrentUser").document(orderDocument.id).get()
                                            .addOnSuccessListener { userDocument ->
                                                val user = userDocument.toObject(CurrentUser::class.java)?: CurrentUser()
                                                if (user != null) {
                                                    order.users = listOf(user)
                                                // Add the user to the order

                                                    // Log the user details for debugging
                                                    Log.d("AllOrdersViewModel", "User details: $user")
                                                }

                                                orders.add(order)

                                                // Emit the list of orders
                                                val sortedOrders = orders.sortedByDescending { it.orderDate }
                                                viewModelScope.launch {
                                                    _allOrders.emit(Resource.Success(sortedOrders))
                                                }
                                            }
                                    }
                                }.addOnFailureListener { error ->
                                    // Log error message for debugging
                                    Log.e("AllOrdersViewModel", "Error fetching items: ${error.message}")

                                    // Handle failure to fetch Items subcollection
                                    viewModelScope.launch {
                                        _allOrders.emit(Resource.Error(error.message.toString()))
                                    }
                                }
                        }
                    }.addOnFailureListener { error ->
                        // Log error message for debugging
                        Log.e("AllOrdersViewModel", "Error fetching orders: ${error.message}")

                        // Handle failure to fetch Orders subcollection
                        viewModelScope.launch {
                            _allOrders.emit(Resource.Error(error.message.toString()))
                        }
                    }
            }
        }
    }


}