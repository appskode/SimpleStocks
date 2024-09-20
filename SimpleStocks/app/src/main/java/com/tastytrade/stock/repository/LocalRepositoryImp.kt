package com.tastytrade.stock.repository

import android.util.Log
import com.tastytrade.stock.data.WatchListDao
import com.tastytrade.stock.model.WatchList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class LocalRepositoryImp @Inject constructor(
    private val watchListDao: WatchListDao
): LocalRepository{

override suspend fun getFirstWatchlist(result: (Resource<WatchList>) -> Unit) {
        try{
            val res = watchListDao.getFirstWatchList()
            result.invoke(
                Resource.Success(res)
            )
        }catch (e: Exception){
            result.invoke(
                Resource.Error(e)
            )
        }
    }

    override suspend fun getAllWatchlist(result: (Resource<List<String>>) -> Unit) {
        try{
            val res = watchListDao.getAllWatchLists()
            result.invoke(
                Resource.Success(res)
            )
        }catch (e: Exception){
            result.invoke(
                Resource.Error(e)
            )
        }
    }

    override suspend fun getWatchList(name: String, result: (Resource<WatchList>) -> Unit) {
        try{
            val res = watchListDao.getWatchList(name)
            result.invoke(
                Resource.Success(res)
            )
        }catch (e: Exception){
            result.invoke(
                Resource.Error(e)
            )
        }
    }

    override suspend fun addWatchlist(watchList: WatchList) {
        try {
            watchListDao.insert(watchList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteWatchlist(name: String) {
        watchListDao.delete(name)
    }

    override suspend fun updateWatchListSymbols(name: String, symbols: List<String>) {
        watchListDao.updateSymbols(name, symbols)
    }

    override suspend fun updateWatchListName(oldName: String, newName: String, result: (Resource<String>) -> Unit) {
        try{
            watchListDao.updateName(oldName, newName)
            result.invoke(
                Resource.Success(newName)
            )
        }catch (e: Exception){
            result.invoke(
                Resource.Error(java.lang.Exception("Error updating watchlist name, Name already exists"))
            )
        }
    }
}
