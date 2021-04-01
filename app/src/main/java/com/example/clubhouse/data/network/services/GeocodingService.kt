package com.example.clubhouse.data.network.services

import com.example.clubhouse.data.helpers.ContactAddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("json")
    suspend fun reverseGeocode(
        @Query("latlng") latLng: String,
        @Query("key") key: String,
        @Query("language") language: String
    ): ContactAddressResponse
}