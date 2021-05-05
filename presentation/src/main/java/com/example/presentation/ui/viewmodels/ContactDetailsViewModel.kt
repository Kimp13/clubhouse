package com.example.presentation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.ContactEntity
import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

class ContactDetailsViewModel(
    private val interactor: ContactDetailsInteractor
) : ViewModel() {
    val error: LiveData<Unit>
        get() = mutableError

    val contact: LiveData<ContactEntity>
        get() = mutableContact

    private val mutableError = MutableLiveData<Unit>()
    private val mutableContact = MutableLiveData<ContactEntity>()

    fun setContactLookup(lookup: String) {
        if (mutableContact.value != null) {
            mutableContact.value = mutableContact.value
        }

        refreshContactDetails(lookup)
    }

    fun refreshContactDetails(lookup: String) {
        viewModelScope.launch {
            try {
                interactor.getContact(lookup)?.let {
                    mutableContact.postValue(it)
                } ?: mutableError.postValue(Unit)
            } catch (e: CancellationException) {
                Timber.d("ContactDetailsViewModel job cancelled\n$e")
            }
        }
    }
}