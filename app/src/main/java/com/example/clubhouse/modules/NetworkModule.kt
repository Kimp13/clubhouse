package com.example.clubhouse.modules

import com.example.clubhouse.BuildConfig
import com.example.presentation.data.apis.DirectionsApi
import com.example.presentation.data.apis.GeocodingApi
import com.example.presentation.data.interceptors.InterceptorImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import okhttp3.Interceptor
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
    fun provideInterceptor(): Interceptor = InterceptorImpl()

    @Provides
    @Singleton
    fun provideGeocodingApi(
        gson: Gson,
        interceptor: Interceptor
    ): GeocodingApi = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        )
        .baseUrl(BuildConfig.GOOGLE_GEOCODING_URI)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(GeocodingApi::class.java)

    @Provides
    @Singleton
    fun provideDirectionsApi(
        gson: Gson,
        interceptor: Interceptor
    ): DirectionsApi = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        )
        .baseUrl(BuildConfig.GOOGLE_DIRECTIONS_API)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(DirectionsApi::class.java)
}
