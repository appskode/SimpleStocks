package com.tastytrade.stock.api

import com.tastytrade.stock.model.ChartResponseData
import com.tastytrade.stock.model.Symbol
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Named

interface WatchListAPI {
    @GET("stock/market/batch")
    suspend fun getWatchLists(
        @Query("token") token: String,
        @Query("symbols") symbols: String,
        @Query("types") types: String
    ): Response<HashMap<String, Symbol>>

    @GET("stock/{SYMBOL}/chart/1m")
    suspend fun getChartData(
        @Path("SYMBOL") symbol: String,
        @Query("token") token: String
    ): Response<ChartResponseData>
}
