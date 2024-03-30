package com.example.shoplo.data


data class Shop(
    val sellerID: String,
val fullName: String,
val icNumber: String,
val hpNumber: String
){
    constructor():this("","","", "")
}

