package com.example.presentation.data.apis

import com.example.presentation.data.entities.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {
    @GET("json")
    suspend fun navigate(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String,
    ): DirectionsResponse
}
