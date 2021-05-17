package com.example.domain.repositories.interfaces

import com.example.domain.entities.ContactEntity
import com.example.domain.entities.SimpleContactEntity

interface ContactRepository {
    suspend fun getSimpleContacts(query: String?): List<SimpleContactEntity>?

    suspend fun getContacts(lookups: List<String?>): List<ContactEntity>

    suspend fun getContact(lookup: String): ContactEntity?
}