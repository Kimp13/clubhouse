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

    fun loadContacts(contactId: Long?) {
        contacts.value?.let {
            mutableContacts.value = it
        }

        viewModelScope.launch {
            try {
                mutableContacts.postValue(
                    contactId?.let { id ->
                        interactor.findContactById(id)?.let {
                            listOf(it)
                        } ?: emptyList()
                    } ?: interactor.getAllContactsWithLocation())
            } catch (e: CancellationException) {
                Timber.e(e)
            }
        }
    }
}