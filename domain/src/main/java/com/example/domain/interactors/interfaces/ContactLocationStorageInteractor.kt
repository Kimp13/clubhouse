package com.example.domain.interactors.interfaces

import com.example.domain.entities.ContactLocation

interface ContactLocationStorageInteractor {
    suspend fun findContactLocationById(id: Long): ContactLocation?
    suspend fun addContactLocation(locationEntity: ContactLocation)
}