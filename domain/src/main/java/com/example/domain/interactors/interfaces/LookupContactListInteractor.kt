package com.example.domain.interactors.interfaces

import com.example.domain.entities.ContactEntity

interface LookupContactListInteractor {
    suspend fun getContacts(lookups: List<String>): List<ContactEntity>
}