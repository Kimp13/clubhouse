package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.ContactListInteractor
import com.example.domain.repositories.ContactRepository

class RebootReminderServiceInteractor(
    private val repository: ContactRepository
) : ContactListInteractor {
    override suspend fun getContacts(lookups: List<String>) =
        repository.getContacts(lookups)
}