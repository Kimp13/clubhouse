package com.example.clubhouse.ui.viewmodels

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clubhouse.BuildConfig.MAPS_API_KEY
import com.example.clubhouse.ContactApplication
import com.example.clubhouse.data.daos.ContactLocationDao
import com.example.clubhouse.data.entities.ContactLocation
import com.example.clubhouse.data.network.GeocodingNetwork
import com.example.clubhouse.di.modules.GsonModule
import com.example.clubhouse.di.scopes.ViewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.Subcomponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@Subcomponent(modules = [GsonModule::class])
@ViewModelScope
interface LocationViewModelComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LocationViewModelComponent
    }

    fun inject(viewModel: ContactLocationViewModel)
}

class ContactLocationViewModel(
    application: Application
) : AndroidViewModel(application) {
    var interacted = false

    val location: LiveData<Location>
        get() = mutableLocation

    val address: LiveData<String?>
        get() = mutableAddress

    var currentPoint: LatLng? = null
        set(value) {
            field = value
            interacted = true

            addressJob?.cancel()

            value?.let { point ->
                addressJob = viewModelScope.launch {
                    try {
                        mutableAddress.postValue(
                            withContext(Dispatchers.IO) {
                                geocodingNetwork.reverseGeocode(
                                    point,
                                    MAPS_API_KEY,
                                    Locale.getDefault().language
                                )
                                    .results
                                    .firstOrNull()
                                    ?.formattedAddress
                            }
                        )
                    } catch (e: CancellationException) {
                        Timber.d("ContactLocationViewModel job cancelled\n$e")
                    } catch (e: Throwable) {
                        mutableAddress.postValue(null)
                    }
                }
            }
        }

    @Inject
    lateinit var geocodingNetwork: GeocodingNetwork

    @Inject
    lateinit var contactLocationDao: ContactLocationDao

    private val mutableLocation = MutableLiveData<Location>()
    private val mutableAddress = MutableLiveData<String?>()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    private var addressJob: Job? = null

    init {
        (application as? ContactApplication)?.applicationComponent
            ?.locationViewModelComponent()
            ?.create()
            ?.inject(this)
    }

    override fun onCleared() {
        fusedLocationClient = null

        super.onCleared()
    }

    fun initLocation() {
        checkGeocoder()
        checkLocationClient()
    }

    suspend fun submit(contactId: Long) {
        currentPoint?.run {
            withContext(Dispatchers.IO) {
                contactLocationDao.insertLocation(
                    ContactLocation(
                        contactId,
                        mutableAddress.value,
                        latitude,
                        longitude
                    )
                )
            }
        }
    }

    private fun checkGeocoder() {
        if (geocoder == null) {
            geocoder = Geocoder(
                getApplication(),
                Locale.getDefault()
            )
        }
    }

    private fun checkLocationClient() {
        if (fusedLocationClient == null) {
            try {
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(
                        getApplication() as Context
                    ).apply {
                        lastLocation.addOnSuccessListener {
                            mutableLocation.postValue(it)
                        }
                    }
            } catch (e: SecurityException) {
                Timber.d("Location exception\n$e")
            }
        } else {
            mutableLocation.value = mutableLocation.value
        }
    }
}