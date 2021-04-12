package com.example.clubhouse.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clubhouse.data.ContactRepository
import com.example.clubhouse.data.SimpleContactEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

class ContactListViewModel(application: Application) :
    AndroidViewModel(application) {
    val contactList: LiveData<List<SimpleContactEntity>>
        get() = mutableContactList

    var searchQuery: String? = null
        private set

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
                ContactRepository.getSimpleContacts(
                    getApplication(),
                    query
                )?.let {
                    mutableContactList.postValue(it)
                }
            } catch (e: CancellationException) {
                Timber.d("ContactListViewModel job cancelled\n$e")
            }
        }
    }
}