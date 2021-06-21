package com.example.presentation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.interfaces.ViewContactLocationInteractor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

class ViewContactLocationViewModel(
    private val interactor: ViewContactLocationInteractor
) : ViewModel() {
    val contacts: LiveData<List<ContactEntity>>
        get() = mutableContacts

    private val mutableContacts = MutableLiveData<List<ContactEntity>>()

    fun loadAllContacts() = safeLaunch {
        checkContactsForExistingValue()

        val contactList = interactor.getAllContactsWithLocation()
        mutableContacts.postValue(contactList)
    }

    fun loadContact(contactId: Long) = safeLaunch {
        checkContactsForExistingValue()

        val contact = interactor.findContactById(contactId)
        contact?.let {
            mutableContacts.postValue(listOf(it))
        }
    }

    private fun checkContactsForExistingValue() {
        contacts.value?.let {
            mutableContacts.value = it
        }
    }

    private fun safeLaunch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                Timber.e(e)
            }
        }
    }
}
