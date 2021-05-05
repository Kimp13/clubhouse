package com.example.domain.entities

class ContactEntity(
    val id: Long,
    val lookup: String,
    val name: String? = null,
    val description: String? = null,
    val birthDate: BirthDate? = null,
    val photoId: Long? = null,
    val location: ContactLocationEntity? = null,
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList()
)