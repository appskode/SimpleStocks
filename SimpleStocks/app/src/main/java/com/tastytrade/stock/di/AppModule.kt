package com.tastytrade.stock.di

import android.content.Context
import androidx.room.Room
import com.tastytrade.stock.data.WatchListDao
import com.tastytrade.stock.data.WatchListLocalDB
import com.tastytrade.stock.repository.LocalRepository
import com.tastytrade.stock.repository.LocalRepositoryImp
import com.tastytrade.stock.repository.StocksRepository
import com.tastytrade.stock.repository.StocksRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideWatchlistRepository(impl: StocksRepositoryImpl): StocksRepository = impl

    @Provides
    fun provideLocalRepository(impl: LocalRepositoryImp): LocalRepository = impl

    @Provides
    fun providesUserDatabaseInstance(@ApplicationContext context: Context): WatchListLocalDB {
        return Room.databaseBuilder(
            context, WatchListLocalDB::class.java, "Watchlist_database"
        ).build()
    }

    @Provides
    fun providesWatchlistDaoInstance(database: WatchListLocalDB): WatchListDao =
        database.watchListDao()
}
