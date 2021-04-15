package com.example.clubhouse.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.clubhouse.data.ContactEntity
import com.example.clubhouse.data.ContactRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ContactDetailsViewModel(application: Application) : AndroidViewModel(
    application
) {
    val throwable: LiveData<Throwable>
        get() = mutableThrowable
    val contact: LiveData<ContactEntity>
        get() = mutableContact

    private val mutableThrowable = MutableLiveData<Throwable>()
    private val mutableContact = MutableLiveData<ContactEntity>()
    private var disposable: Disposable? = null

    override fun onCleared() {
        disposable?.dispose()

        super.onCleared()
    }

    fun setContactLookup(lookup: String) {
        if (mutableContact.value != null) {
            mutableContact.value = mutableContact.value
        }

        refreshContactDetails(lookup)
    }

    fun refreshContactDetails(lookup: String) {
        disposable?.dispose()

        disposable = ContactRepository.getContact(
            getApplication(),
            lookup
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { contact: ContactEntity ->
                    mutableContact.postValue(contact)
                },
                onError = {
                    mutableThrowable.postValue(it)
                }
            )
    }
}