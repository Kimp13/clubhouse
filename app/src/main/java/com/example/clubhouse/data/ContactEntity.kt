package com.example.clubhouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactEntity(
    val id: Long,
    val lookup: String,
    val name: String? = null,
    val description: String? = null,
    val birthDate: BirthDate? = null,
    val photoId: Long? = null,
    val emails: List<String> = listOf(),
    val phoneNumbers: List<String> = listOf()
) : Parcelable
