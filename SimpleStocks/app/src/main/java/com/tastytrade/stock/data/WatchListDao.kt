package com.tastytrade.stock.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tastytrade.stock.model.WatchList

@Dao
interface WatchListDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(watchList: WatchList)

    @Query("UPDATE WatchList SET symbolList = :symbols WHERE name = :name")
    suspend fun updateSymbols(name: String, symbols: List<String>)

    @Query("UPDATE WatchList SET name = :newName WHERE name = :oldName")
    suspend fun updateName(oldName: String, newName: String)

    @Query("DELETE FROM WatchList WHERE name = :name")
    suspend fun delete(name: String)

    @Query("SELECT name FROM WatchList")
    suspend fun getAllWatchLists(): List<String>

    @Query("SELECT * FROM WatchList WHERE name = :name")
    suspend fun getWatchList(name: String): WatchList

    @Query("SELECT * FROM WatchList LIMIT 1")
    suspend fun getFirstWatchList(): WatchList
}
