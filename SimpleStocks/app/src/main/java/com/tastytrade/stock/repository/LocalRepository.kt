package com.tastytrade.stock.repository

import com.tastytrade.stock.model.WatchList

interface LocalRepository {
    suspend fun getAllWatchlist(result: (Resource<List<String>>) -> Unit)
    suspend fun getWatchList(name: String, result: (Resource<WatchList>) -> Unit)
    suspend fun addWatchlist(watchList: WatchList)
    suspend fun deleteWatchlist(name: String)
    suspend fun getFirstWatchlist(result: (Resource<WatchList>) -> Unit)
    suspend fun updateWatchListSymbols(name: String, symbols: List<String>)
    suspend fun updateWatchListName(oldName: String, newName: String, result: (Resource<String>) -> Unit)
}
