package com.example.clubhouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleContactEntity(
    val name: String,
    val phoneNumber: String?
) : Parcelable