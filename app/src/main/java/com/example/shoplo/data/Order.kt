package com.example.shoplo.data

import android.os.Parcelable
import com.google.firebase.firestore.auth.User

import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val customerID: String = "",
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val products: List<Item> = listOf(),
    val orderTotal: Double = 0.0 ,
    val orderDate: String = "",
    val orderTime: String = "",
    val orderId: String = "",
    val address: CurrentUser = CurrentUser(),
    var items: List<Item> = listOf()
): Parcelable
