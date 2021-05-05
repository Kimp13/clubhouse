package com.example.domain.interactors.interfaces

import com.example.domain.entities.ContactEntity

interface ContactDetailsInteractor {
    suspend fun getContact(lookup: String): ContactEntity?
}