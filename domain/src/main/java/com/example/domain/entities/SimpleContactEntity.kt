package com.example.domain.entities

data class SimpleContactEntity(
    val id: Long,
    val lookup: String,
    val name: String? = null,
    var phoneNumber: String? = null,
    var photoId: Long? = null
)
