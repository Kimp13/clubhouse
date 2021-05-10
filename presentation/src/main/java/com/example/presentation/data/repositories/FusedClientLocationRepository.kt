package com.example.presentation.data.repositories

import android.content.Context
import com.example.domain.entities.LocationEntity
import com.example.domain.repositories.LastLocationRepository
import com.google.android.gms.location.LocationServices
import timber.log.Timber

class FusedClientLocationRepository(
    context: Context
) : LastLocationRepository {
    private val client = LocationServices.getFusedLocationProviderClient(
        context
    )

    override fun getLastLocation(onSuccess: (LocationEntity?) -> Unit) {
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
}