package com.tastytrade.stock.viewmodel

import androidx.lifecycle.ViewModel
import com.tastytrade.stock.model.WatchList
import kotlinx.coroutines.flow.MutableStateFlow

open class BaseViewModel : ViewModel() {
    companion object {
        val currentWatchList = MutableStateFlow<WatchList?>(null)
    }

    fun setCurrentWatchList(watchList: WatchList) {
        currentWatchList.value = watchList
    }

    fun getCurrentWatchList(): WatchList? {
        return currentWatchList.value
    }
}
