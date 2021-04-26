package com.example.clubhouse.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubhouse.data.entities.ContactEntity
import com.example.clubhouse.data.repositories.ContactRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(
    private val repository: ContactRepository
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
                repository.getContact(lookup)?.let {
                    mutableContact.postValue(it)
                } ?: mutableError.postValue(Unit)
            } catch (e: CancellationException) {
                Timber.d("ContactDetailsViewModel job cancelled\n$e")
            }
        }
    }
}