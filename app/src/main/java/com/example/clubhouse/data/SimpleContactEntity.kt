package com.example.clubhouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleContactEntity(
    val id: Long,
    val lookup: String,
    val name: String? = null,
    var phoneNumber: String? = null,
    var photoId: Long? = null
) : Parcelable