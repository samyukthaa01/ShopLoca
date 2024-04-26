package com.example.shoplo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    val bio: String = "",

    val email: String = " ",
    val id: String = "",
    val phone: String = "",
    val username: String = "",
    val profileImg: String = "",
): Parcelable
