package com.example.presentation.data.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class DirectionsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}