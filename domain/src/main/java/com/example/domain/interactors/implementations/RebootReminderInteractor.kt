package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.LookupContactListInteractor
import com.example.domain.repositories.ContactRepository

class RebootReminderInteractor(
    private val repository: ContactRepository
) : LookupContactListInteractor {
    override suspend fun getContacts(lookups: List<String>) =
        repository.getContacts(lookups)
}