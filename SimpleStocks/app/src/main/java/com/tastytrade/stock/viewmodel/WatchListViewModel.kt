package com.tastytrade.stock.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tastytrade.stock.model.Symbol
import com.tastytrade.stock.model.WatchList
import com.tastytrade.stock.repository.LocalRepository
import com.tastytrade.stock.repository.Resource
import com.tastytrade.stock.repository.StocksRepository
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val repository: StocksRepository,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    private val _symbolsDataFlow = MutableStateFlow<Resource<HashMap<String, Symbol>>?>(null)
    val symbolsDataFlow = _symbolsDataFlow.asStateFlow()

    private val _allWatchList = MutableStateFlow<Resource<List<String>>?>(null)
    val allWatchList = _allWatchList.asStateFlow()

    private val _watchList = MutableStateFlow<Resource<WatchList>?>(null)
    val watchList = _watchList.asStateFlow()

    private val _updateName = MutableStateFlow<Resource<String>?>(null)
    val updateName = _updateName.asStateFlow()

    fun getFirstWatchList() = viewModelScope.launch {
        localRepository.getFirstWatchlist {
            _watchList.value = it
        }
    }

    fun getWatchListData(token: String, symbols: String, types: String) = viewModelScope.launch {
        _symbolsDataFlow.value = Resource.Loading
        _symbolsDataFlow.value = repository.getWatchListData(token, symbols, types)
    }

    fun addWatchList(watchList: WatchList) = viewModelScope.launch {
        localRepository.addWatchlist(watchList)
    }


    fun getAllWatchList() = viewModelScope.launch {
        _allWatchList.value = Resource.Loading
        localRepository.getAllWatchlist {
            _allWatchList.value = it
        }
    }

    fun deleteWatchList(name: String) = viewModelScope.launch {
        localRepository.deleteWatchlist(name)
    }

    fun getWatchList(name: String) = viewModelScope.launch {
        _watchList.value = Resource.Loading
        localRepository.getWatchList(name) {
            _watchList.value = it
        }
    }

    fun updateWatchListName(oldName: String, newName: String) = viewModelScope.launch {
        localRepository.updateWatchListName(oldName, newName) {
            _updateName.value = it
        }
    }
}
