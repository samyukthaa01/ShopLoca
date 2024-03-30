package com.example.shoplo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val productId: String, // provide default values for each property
    val productName: String ,
    val productCategory: String,
    val price: Float ,
    val offerPercentage: Float? = null,
    val productDescription: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val productImages: List<String>,
    val shopName: String,
    val sellerID: String,
    val sellerName: String
): Parcelable{
    constructor(): this(
        productId = "0",
        productName = "",
        productCategory = "",
        price = 0f,
        offerPercentage = null,
        productDescription = null,
        colors = null,
        sizes = null,
        productImages = emptyList(),
        shopName = "",
        sellerID = "",
        sellerName = ""
    )
}

