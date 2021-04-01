package com.example.clubhouse.data.network

import com.example.clubhouse.data.network.interceptors.GeocodingInterceptor
import com.example.clubhouse.data.network.services.GeocodingService
import com.example.clubhouse.di.scopes.ViewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

private const val GOOGLE_GEOCODING_URL =
    "https://maps.googleapis.com/maps/api/geocode/"

@ViewModelScope
class GeocodingNetwork @Inject constructor(
    gson: Gson
) {
    private val service = Retrofit.Builder()
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(GeocodingInterceptor())
                .build()
        )
        .baseUrl(GOOGLE_GEOCODING_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(GeocodingService::class.java)

    suspend fun reverseGeocode(
        latLng: LatLng,
        key: String,
        language: String = "en"
    ) = service.reverseGeocode(
        "${latLng.latitude},${latLng.longitude}",
        key,
        language
    )
}