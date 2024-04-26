package com.example.shoplo.viewmodel

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

        // Create an empty list to hold the item IDs
        val itemIDs = mutableListOf<String>()

        // Get a reference to the Order collection
        val orderRef = firestore.collection("Order")

        // Fetch all documents from the Order collection
        orderRef.get().addOnSuccessListener { orderDocuments ->
            for (orderDocument in orderDocuments) {
                // For each order, fetch the Orders subcollection
                orderDocument.reference.collection("Orders").get()
                    .addOnSuccessListener { ordersDocuments ->
                        for (ordersDocument in ordersDocuments) {
                            // For each Orders document, fetch the Items subcollection
                            ordersDocument.reference.collection("Items").get()
                                .addOnSuccessListener { itemsDocuments ->
                                    val items = mutableListOf<Item>()
                                    for (itemDocument in itemsDocuments) {
                                        // Create an Item object from the retrieved document
                                        val itemId = itemDocument.getString("productId") ?: ""
                                        val productName = itemDocument.getString("productName") ?: ""
                                        val productPrice = itemDocument.getString("productPrice") ?: ""
                                        val totalPrice = itemDocument.getDouble("totalPrice") ?: 0.0f
                                        val totalQuantity = itemDocument.getLong("totalQuantity")?.toInt() ?: 0
                                        val productImage = itemDocument.get("productImage") as List<String> // assuming productImage is a List<String>

                                        val item = Item(itemId, productImage, productName, productPrice, totalPrice, totalQuantity)
                                        // Add the Item object to the list
                                        items.add(item)
                                    }

                                    // Now you have the list of items associated with this order
                                    // Update your Order object to include this list of items
                                    val order = ordersDocument.toObject(Order::class.java)
                                    order.items = items

                                    // Add the order to the list
                                    orders.add(order)

                                    // Emit the list of orders
                                    val sortedOrders = orders.sortedByDescending { it.orderDate }
                                    viewModelScope.launch {
                                        _allOrders.emit(Resource.Success(sortedOrders))
                                    }
                                }.addOnFailureListener { error ->
                                // Handle failure to fetch Items subcollection
                                viewModelScope.launch {
                                    _allOrders.emit(Resource.Error(error.message.toString()))
                                }
                            }
                        }
                    }.addOnFailureListener { error ->
                    // Handle failure to fetch Orders subcollection
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Error(error.message.toString()))
                    }
                }

            }
        }
    }}