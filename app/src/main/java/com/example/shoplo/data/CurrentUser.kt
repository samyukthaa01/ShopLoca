package com.example.shoplo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    val bio: String? = null,
    val email: String? = null,
    val userAdress: String? = null,
    val id: String? = null,
    val phone: String? = null,
    val username: String? = null,
    val profileImg: String? = null,
): Parcelable
