package com.example.presentation.data.apis

import com.example.presentation.data.entities.ContactAddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("json")
    suspend fun reverseGeocode(
        @Query("latlng") latLng: String,
        @Query("key") key: String,
        @Query("language") language: String
    ): ContactAddressResponse
}
