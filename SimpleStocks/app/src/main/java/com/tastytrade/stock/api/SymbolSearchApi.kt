package com.tastytrade.stock.api

import com.tastytrade.stock.model.SymbolSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Named

interface SymbolSearchApi {
    @GET("{SYMBOL}")
    suspend fun getSymbolSearchResults(
        @Path("SYMBOL") symbol: String,
    ): Response<SymbolSearchResponse>
}
