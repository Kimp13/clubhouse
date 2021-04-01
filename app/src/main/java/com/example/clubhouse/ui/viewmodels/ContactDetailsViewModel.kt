package com.example.clubhouse.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clubhouse.ContactApplication
import com.example.clubhouse.data.entities.ContactEntity
import com.example.clubhouse.data.repositories.ContactRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ContactDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {
    val error: LiveData<Unit>
        get() = mutableError

    val contact: LiveData<ContactEntity>
        get() = mutableContact

    @Inject
    lateinit var repository: ContactRepository

    private val mutableError = MutableLiveData<Unit>()
    private val mutableContact = MutableLiveData<ContactEntity>()

    init {
        (application as? ContactApplication)?.applicationComponent
            ?.contactViewModelComponent()
            ?.create()
            ?.inject(this)
    }

    fun setContactLookup(lookup: String) {
        if (mutableContact.value != null) {
            mutableContact.value = mutableContact.value
        }

        refreshContactDetails(lookup)
    }

    fun refreshContactDetails(lookup: String) {
        viewModelScope.launch {
            try {
                repository.getContact(getApplication(), lookup)?.let {
                    mutableContact.postValue(it)
                } ?: mutableError.postValue(Unit)
            } catch (e: CancellationException) {
                Timber.d("ContactDetailsViewModel job cancelled\n$e")
            }
        }
    }
}