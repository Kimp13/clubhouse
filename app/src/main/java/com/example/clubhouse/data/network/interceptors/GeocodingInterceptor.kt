package com.example.clubhouse.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class GeocodingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}