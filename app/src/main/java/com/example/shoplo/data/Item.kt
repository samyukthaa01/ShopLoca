package com.example.shoplo.data

import android.os.Parcelable

import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Item(
val productImage: List<String>,
val productsName: String ,
val productPrice: Float ,
    val totalPrice: Float = 0f,
    val totalQuantity: Int


): Parcelable
