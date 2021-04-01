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

class ContactListViewModel(application: Application) : AndroidViewModel(application) {
    val contactList: LiveData<List<SimpleContactEntity>>
        get() = mutableContactList

    private val mutableContactList =
        MutableLiveData<List<SimpleContactEntity>>()

    fun search() {
        if (mutableContactList.value != null) {
            mutableContactList.value = mutableContactList.value
        }

        refreshContactList()
    }

    fun refreshContactList() {
        viewModelScope.launch {
            try {
                ContactRepository.getSimpleContacts(getApplication())?.let {
                    mutableContactList.postValue(it)
                }
            } catch (e: CancellationException) {
                Timber.d("ContactListViewModel job cancelled\n$e")
            }
        }
    }
}