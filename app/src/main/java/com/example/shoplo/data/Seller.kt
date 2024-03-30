package com.example.shoplo.data

data class Seller(
    val shopID: String,
    val shopName: String,
    val address: String,
    val email: String,
    val imagePath: String = ""
){
   constructor(): this("", "", "","")
    //constructor(fullName: String, icNumber: String, hpNumber: String) :
          //  this(fullName, icNumber, hpNumber, "","","")
}

