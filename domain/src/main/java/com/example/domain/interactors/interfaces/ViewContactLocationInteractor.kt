package com.example.domain.interactors.interfaces

import com.example.domain.entities.ContactEntity

interface ViewContactLocationInteractor {
    suspend fun findContactById(id: Long): ContactEntity?

    suspend fun getAllContactsWithLocation(): List<ContactEntity>
}