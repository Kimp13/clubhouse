package com.example.presentation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.interactors.implementations.ContactLocationInteractor
import com.example.presentation.data.toLocationEntity
import com.example.presentation.ui.viewmodels.states.ContactLocationState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ContactLocationViewModel(
    private val interactor: ContactLocationInteractor
) : ViewModel() {
    var interacted = false

    val state: LiveData<ContactLocationState>
        get() = mutableState

    var currentPoint: LatLng? = null
        set(value) {
            field = value
            interacted = true

            addressJob?.cancel()
            mutableState.postValue(
                mutableState.value?.copy(
                    progress = true,
                    address = null
                )
            )

            value?.toLocationEntity()?.let { point ->
                addressJob = viewModelScope.launch {
                    try {
                        interactor.reverseGeocode(
                            point,
                            Locale.getDefault().language
                        )
                            .let {
                                mutableState.postValue(
                                    mutableState.value?.copy(
                                        address = it,
                                        progress = false
                                    )
                                )
                            }
                    } catch (e: Throwable) {
                        Timber.e(e)

                        mutableState.postValue(
                            mutableState.value?.copy(
                                address = null,
                                progress = false
                            )
                        )
                    }
                }
            }
        }

    private var addressJob: Job? = null
    private val mutableState = MutableLiveData(ContactLocationState())

    fun initLocation() {
        mutableState.value?.location?.let {
            mutableState.value = mutableState.value
        } ?: interactor.getLastLocation { location ->
            location?.let {
                mutableState.postValue(
                    mutableState.value?.copy(
                        location = it
                    )
                )
            }
        }
    }

    fun submit(contactId: Long) {
        mutableState.postValue(
            mutableState.value?.copy(
                progress = true
            )
        )

        viewModelScope.launch {
            currentPoint?.run {
                /* TODO: запись в БД */
            }

            mutableState.postValue(
                mutableState.value?.copy(
                    progress = false,
                    locationWritten = true
                )
            )
        }
    }

    fun checkIfMapControlsClarified() {
        viewModelScope.launch {
            interactor.areMapControlsClarified().let {
                mutableState.postValue(
                    mutableState.value?.copy(
                        areMapControlsClarified = it
                    )
                )
            }
        }
    }

    fun writeMapControlsClarified() {
        viewModelScope.launch {
            interactor.writeMapControlsClarified()
        }
    }
}