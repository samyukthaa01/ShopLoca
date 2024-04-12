package com.example.shoplo.data
import android.os.Parcelable

import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Post(
    val postTitle: String = "",
    val postImages: List<String> = listOf(),
    val timestamp: Long = System.currentTimeMillis()
    // Add more fields here that match the structure of your Firestore documents
): Parcelable

