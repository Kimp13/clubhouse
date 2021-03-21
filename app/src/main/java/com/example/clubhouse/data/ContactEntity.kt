package com.example.clubhouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactEntity(
    val id: Int,
    val name: String,
    val phoneNumbers: List<String>,
    val emails: List<String>,
    val description: String,
    val birthDate: BirthDate
) : Parcelable