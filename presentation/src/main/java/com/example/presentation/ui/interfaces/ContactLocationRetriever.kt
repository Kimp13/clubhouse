package com.example.presentation.ui.interfaces

import com.example.domain.entities.ContactEntity

interface ContactLocationRetriever {
    fun retrieveContactLocation(contact: ContactEntity)
}
