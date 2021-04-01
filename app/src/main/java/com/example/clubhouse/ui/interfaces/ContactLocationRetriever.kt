package com.example.clubhouse.ui.interfaces

import com.example.clubhouse.data.entities.ContactEntity

interface ContactLocationRetriever {
    fun retrieveContactLocation(contact: ContactEntity)
}