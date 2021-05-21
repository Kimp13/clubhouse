package com.example.clubhouse.modules

import com.example.clubhouse.BuildConfig
import com.example.presentation.data.apis.GeocodingApi
import com.example.presentation.data.interceptors.GeocodingInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideGeocodingApi(
        gson: Gson
    ) = Retrofit.Builder()
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(GeocodingInterceptor())
                .build()
        )
        .baseUrl(BuildConfig.GOOGLE_GEOCODING_URI)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(GeocodingApi::class.java)
}
