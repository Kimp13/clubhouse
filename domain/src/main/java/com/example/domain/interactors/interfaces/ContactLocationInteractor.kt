package com.example.domain.interactors.interfaces

import com.example.domain.entities.ContactLocation

interface ContactLocationInteractor {
    suspend fun findContactLocationById(id: Long): ContactLocation?
}
