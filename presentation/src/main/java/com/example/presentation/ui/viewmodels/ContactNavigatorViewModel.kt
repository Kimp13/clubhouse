package com.example.presentation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.LocationEntity
import com.example.domain.entities.toLocationEntity
import com.example.domain.interactors.implementations.ContactNavigatorInteractor
import com.example.presentation.data.entities.ParcelableSimpleContact
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

class ContactNavigatorViewModel(
    private val interactor: ContactNavigatorInteractor
) : ViewModel() {
    val steps: LiveData<List<LocationEntity>?>
        get() = mutableSteps

    private val mutableSteps =
        MutableLiveData<List<LocationEntity>?>()

    fun getSteps(
        contacts: Pair<ParcelableSimpleContact, ParcelableSimpleContact>
    ) {
        mutableSteps.value?.let {
            mutableSteps.value = it
        }

        viewModelScope.launch {
            try {
                val edgeLocations = listOf(
                    async {
                        interactor.findContactLocationById(contacts.first.id)
                    },
                    async {
                        interactor.findContactLocationById(contacts.second.id)
                    }
                )
                    .awaitAll()
                    .filterNotNull()
                    .takeIf { it.size == 2 }
                    ?.let {
                        it[0].toLocationEntity() to it[1].toLocationEntity()
                    }

                mutableSteps.postValue(
                    edgeLocations?.let { locations ->
                        interactor.navigate(
                            locations.first,
                            locations.second
                        )
                            ?.let {
                                listOf(locations.first)
                                    .plus(it)
                                    .plus(listOf(locations.second))
                            }
                    }
                )
            } catch (e: CancellationException) {
                Timber.e(e)
            }
        }
    }
}
