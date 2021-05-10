package com.example.domain.repositories.interfaces

import com.example.domain.entities.ContactEntity
import com.example.domain.entities.SimpleContactEntity

interface ContactRepository {
    suspend fun getSimpleContacts(
        query: String?,
        contactsIds: List<Long>?
    ): List<SimpleContactEntity>?

    suspend fun getContacts(lookups: List<String?>): List<ContactEntity>

    suspend fun findContactsById(ids: List<Long>): List<ContactEntity>

    suspend fun getContact(lookup: String): ContactEntity?

    suspend fun findContactById(id: Long): ContactEntity?
}