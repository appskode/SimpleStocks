package com.tastytrade.stock.api

import com.tastytrade.stock.utils.Constants.TOKEN
import okhttp3.Interceptor
import javax.inject.Inject

class NetworkInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder().addHeader("Token", "Bearer $TOKEN")
        return chain.proceed(request.build())
    }
}
