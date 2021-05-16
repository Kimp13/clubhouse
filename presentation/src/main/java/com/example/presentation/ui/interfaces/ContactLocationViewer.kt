package com.example.presentation.ui.interfaces

import com.example.domain.entities.ContactEntity

interface ContactLocationViewer {
    fun viewContactLocation(contactEntity: ContactEntity)
    fun viewAllContactsLocation()
}