package com.tastytrade.stock.repository

import com.tastytrade.stock.model.ChartResponseData
import com.tastytrade.stock.model.Item
import com.tastytrade.stock.model.Symbol
import com.tastytrade.stock.model.SymbolSearchResponse

interface StocksRepository {
    suspend fun getWatchListData(
        token: String,
        symbols: String,
        types: String
    ): Resource<HashMap<String, Symbol>>

    suspend fun getSymbolSearchResults(
        symbol: String
    ): Resource<SymbolSearchResponse>

    suspend fun getChartData(symbol: String, token: String): Resource<ChartResponseData>
}
