package com.tastytrade.stock.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tastytrade.stock.model.WatchList

@Database(entities = [WatchList::class], version = 1)
@TypeConverters(StringListConverter::class)
abstract class WatchListLocalDB : RoomDatabase() {
    abstract fun watchListDao(): WatchListDao
}
