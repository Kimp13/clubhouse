package com.example.presentation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.SimpleContactEntity
import com.example.domain.interactors.interfaces.SimpleContactListInteractor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

class ContactListViewModel(
    private val interactor: SimpleContactListInteractor
) : ViewModel() {
    val error: LiveData<Unit>
        get() = mutableError

    val contactList: LiveData<List<SimpleContactEntity>>
        get() = mutableContactList

    var searchQuery: String? = null
        private set

    private val mutableError = MutableLiveData<Unit>()
    private val mutableContactList =
        MutableLiveData<List<SimpleContactEntity>>()

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
                interactor.getSimpleContacts(query)?.let {
                    mutableContactList.postValue(it)
                } ?: mutableError.postValue(Unit)
            } catch (e: CancellationException) {
                Timber.d("ContactListViewModel job cancelled\n$e")
            }
        }
    }
}
