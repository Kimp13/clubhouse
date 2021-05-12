package com.example.presentation.data.entities

import com.google.gson.annotations.SerializedName

data class ContactAddress(
    @SerializedName("formatted_address")
    val formattedAddress: String
)

data class ContactAddressResponse(
    val results: List<ContactAddress>
)