package com.example.shoplo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import com.example.shoplo.util.Resource
import com.example.shoplo.data.Seller
import com.example.shoplo.data.Shop
import com.example.shoplo.util.Constants.SELLER_COLLECTION
import com.example.shoplo.util.Constants.SHOP_COLLECTION
import com.example.shoplo.util.RegisterFieldState
import com.example.shoplo.util.RegisterValidation
import com.example.shoplo.util.validateEmail
import com.example.shoplo.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _register = MutableStateFlow<Resource<Seller>>(Resource.Unspecified())
    val register: Flow<Resource<Seller>> = _register
    private val _shopRegister = MutableStateFlow<Resource<Shop>>(Resource.Unspecified())
    val shopRegister: Flow<Resource<Shop>> = _shopRegister
    private val _validation = Channel<RegisterFieldState> ()
    val validation = _validation.receiveAsFlow()
    fun createAccountWithEmailAndPassword(user: Seller, shop: Shop, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveSellerInfo(it.uid,user)
                        saveShopInfo(it.uid, shop)
                        // _register.value = Resource.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }else{
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveSellerInfo(userUid: String, user: Seller) {
        db.collection(SELLER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener{
                    e ->
                _register.value = Resource.Error("Firestore Error: ${e.message}")
                Log.e("FirestoreError", "Error writing document", e)
            }


    }
    private fun saveShopInfo(userUid: String, shop: Shop) {
        db.collection(SHOP_COLLECTION)
            .document(userUid)
            .set(shop)
            .addOnSuccessListener {
                _shopRegister.value = Resource.Success(shop)// Optionally, you can handle success or update LiveData for Shop
            }
            .addOnFailureListener { e ->
                _shopRegister.value = Resource.Error("Firestore Error: ${e.message}")
                Log.e("FirestoreError", "Error writing document", e) // Optionally, handle failure or update LiveData for Shop with error
            }
    }
    private fun checkValidation(user: Seller, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}