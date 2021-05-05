package com.example.domain.entities

data class ContactEntity(
    val id: Long,
    val lookup: String,
    val name: String?,
    val phoneNumbers: List<String>,
    val emails: List<String>,
    val description: String? = null,
    val birthDate: BirthDate? = null,
    val photoId: Long? = null
)