package com.example.clubhouse.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clubhouse.ContactApplication
import com.example.clubhouse.data.entities.SimpleContactEntity
import com.example.clubhouse.data.repositories.ContactRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ContactListViewModel(
    application: Application
) : AndroidViewModel(application) {
    val error: LiveData<Unit>
        get() = mutableError

    val contactList: LiveData<List<SimpleContactEntity>>
        get() = mutableContactList

    var searchQuery: String? = null
        private set

    @Inject
    lateinit var repository: ContactRepository

    private val mutableError = MutableLiveData<Unit>()
    private val mutableContactList =
        MutableLiveData<List<SimpleContactEntity>>()

    init {
        (application as? ContactApplication)?.applicationComponent
            ?.contactViewModelComponent()
            ?.create()
            ?.inject(this)
    }

    fun provideContactList(query: String? = null) {
        if (query == null) {
            if (mutableContactList.value != null) {
                mutableContactList.value = mutableContactList.value
            }
        } else {
            searchQuery = query
        }

        refreshContactList(searchQuery)
    }

    fun refreshContactList(query: String? = searchQuery) {
        viewModelScope.launch {
            try {
                repository.getSimpleContacts(
                    getApplication(),
                    query
                )?.let {
                    mutableContactList.postValue(it)
                } ?: mutableError.postValue(Unit)
            } catch (e: CancellationException) {
                Timber.d("ContactListViewModel job cancelled\n$e")
            }
        }
    }
}