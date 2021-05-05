package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.SimpleContactListInteractor
import com.example.domain.repositories.ContactRepository

class ContactListFragmentInteractor(
    private val repository: ContactRepository
) : SimpleContactListInteractor {
    override suspend fun getSimpleContacts(query: String?) =
        repository.getSimpleContacts(query)
}