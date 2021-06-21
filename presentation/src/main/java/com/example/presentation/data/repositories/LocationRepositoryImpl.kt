package com.example.presentation.data.repositories

import android.content.Context
import com.example.domain.entities.ContactLocation
import com.example.domain.entities.LocationEntity
import com.example.domain.repositories.interfaces.LocationRepository
import com.example.presentation.BuildConfig
import com.example.presentation.data.apis.DirectionsApi
import com.example.presentation.data.daos.ContactLocationDao
import com.example.presentation.data.entities.toDatabaseEntity
import com.example.presentation.data.entities.toDomainEntity
import com.example.presentation.data.toLocationEntity
import com.google.android.gms.location.LocationServices
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocationRepositoryImpl(
    context: Context,
    private val contactLocationDao: ContactLocationDao,
    private val directionsApi: DirectionsApi
) : LocationRepository {
    private val client = LocationServices.getFusedLocationProviderClient(
        context
    )

    override suspend fun findContactLocationById(
        id: Long
    ): ContactLocation? {
        return contactLocationDao.findById(id)?.toDomainEntity()
    }

    override suspend fun addContactLocation(
        location: ContactLocation
    ) {
        contactLocationDao.add(location.toDatabaseEntity())
    }

    override fun getUserLastLocation(onSuccess: (LocationEntity?) -> Unit) {
        try {
            client.lastLocation.addOnSuccessListener { location ->
                onSuccess(
                    location?.let {
                        LocationEntity(
                            location.latitude,
                            location.longitude
                        )
                    }
                )
            }
        } catch (e: SecurityException) {
            Timber.e(e)
        }
    }

    override suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ) = withContext(Dispatchers.IO) {
        directionsApi.navigate(
            "${fromLocation.latitude},${fromLocation.longitude}",
            "${toLocation.latitude},${toLocation.longitude}",
            BuildConfig.MAPS_API_KEY
        )
            .routes
            .firstOrNull()
            ?.legs
            ?.firstOrNull()
            ?.steps
            ?.fold(emptyList<LocationEntity>()) { result, step ->
                result.plus(
                    PolyUtil.decode(step.polyline.points).map {
                        it.toLocationEntity()
                    }
                )
            }
    }

    override suspend fun getAll(): List<ContactLocation> {
        return contactLocationDao.getAll()
            .map { it.toDomainEntity() }
    }
}
