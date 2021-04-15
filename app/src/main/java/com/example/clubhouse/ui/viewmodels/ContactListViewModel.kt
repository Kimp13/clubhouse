package com.example.clubhouse.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.clubhouse.data.ContactRepository
import com.example.clubhouse.data.SimpleContactEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ContactListViewModel(application: Application) :
    AndroidViewModel(application) {
    val throwable: LiveData<Throwable>
        get() = mutableThrowable
    val contactList: LiveData<List<SimpleContactEntity>>
        get() = mutableContactList

    var searchQuery: String? = null
        private set

    private val mutableThrowable = MutableLiveData<Throwable>()
    private val mutableContactList =
        MutableLiveData<List<SimpleContactEntity>>()

    private var disposable: Disposable? = null

    override fun onCleared() {
        disposable?.dispose()

        super.onCleared()
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
        disposable?.dispose()

        disposable = ContactRepository.getSimpleContacts(
            getApplication(),
            query
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    mutableContactList.postValue(it)
                },
                onError = {
                    mutableThrowable.postValue(it)
                }
            )
    }
}