package com.example.domain.interactors.interfaces

import com.example.domain.entities.SimpleContactEntity

interface SimpleContactListInteractor {
    suspend fun getSimpleContacts(query: String?): List<SimpleContactEntity>?
}