package com.example.shoplo.data

import android.os.Parcelable

import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Item(
    val itemId: String,
val productImages: List<String>,
val productName: String ,
val productPrice: String ,
    val selectedColor: String,
    val selectedSize: String,
val totalPrice: Number,
val totalQuantity: String


): Parcelable
