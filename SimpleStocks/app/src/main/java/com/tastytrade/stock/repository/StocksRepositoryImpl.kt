package com.tastytrade.stock.repository

import com.tastytrade.stock.api.SymbolSearchApi
import com.tastytrade.stock.api.WatchListAPI
import com.tastytrade.stock.model.ChartResponseData
import com.tastytrade.stock.model.Item
import com.tastytrade.stock.model.Symbol
import com.tastytrade.stock.model.SymbolSearchResponse
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class StocksRepositoryImpl @Inject constructor(
    private val watchListAPI: WatchListAPI,
    private val symbolSearchAPI: SymbolSearchApi
) : StocksRepository {

    override suspend fun getWatchListData(token: String, symbols: String, types: String): Resource<HashMap<String, Symbol>> {
        return try {
            getResponse(watchListAPI.getWatchLists(token, symbols, types))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getSymbolSearchResults(symbol: String): Resource<SymbolSearchResponse> {
        return try {
            getResponse(symbolSearchAPI.getSymbolSearchResults(symbol))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getChartData(symbol: String, token: String): Resource<ChartResponseData> {
        return try {
            getResponse(watchListAPI.getChartData(symbol, token))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun <T> getResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            response.body()?.let { result ->
                Resource.Success(result)
            } ?: Resource.Error(Exception("An unknown error occurred"))
        } else {
            Resource.Error(
                Exception(
                    JSONObject(
                        response.errorBody()!!.charStream().readText()
                    ).getString("message")
                )
            )
        }
    }
}
