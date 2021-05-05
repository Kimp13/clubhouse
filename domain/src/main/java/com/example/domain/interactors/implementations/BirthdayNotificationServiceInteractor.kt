package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.repositories.ContactRepository

class BirthdayNotificationServiceInteractor(
    private val repository: ContactRepository
) : ContactDetailsInteractor {
    override suspend fun getContact(lookup: String) =
        repository.getContact(lookup)
}