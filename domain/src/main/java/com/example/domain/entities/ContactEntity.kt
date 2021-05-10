package com.example.domain.entities

data class ContactEntity(
    val id: Long,
    val lookup: String,
    val name: String? = null,
    val description: String? = null,
    val birthDate: BirthDate? = null,
    val photoId: Long? = null,
    val location: ContactLocation? = null,
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList()
)