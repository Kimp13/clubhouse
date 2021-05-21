package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.SimpleContactListInteractor
import com.example.domain.repositories.interfaces.ContactRepository

class SimpleContactListInteractorImpl(
    private val repository: ContactRepository
) : SimpleContactListInteractor {
    override suspend fun getSimpleContacts(query: String?) =
        repository.getSimpleContacts(query)
}
