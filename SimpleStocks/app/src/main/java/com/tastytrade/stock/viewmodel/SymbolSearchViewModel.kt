package com.tastytrade.stock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tastytrade.stock.model.SymbolSearchResponse
import com.tastytrade.stock.model.WatchList
import com.tastytrade.stock.repository.LocalRepository
import com.tastytrade.stock.repository.Resource
import com.tastytrade.stock.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymbolSearchViewModel @Inject constructor(
    private val repository: StocksRepository,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    private val _symbolSearchDataFlow = MutableStateFlow<Resource<SymbolSearchResponse>?>(null)
    val symbolSearchDataFlow = _symbolSearchDataFlow.asStateFlow()

    private val _watchList = MutableStateFlow<Resource<WatchList>?>(null)
    val watchList = _watchList.asStateFlow()

    fun getSymbolSearchData(query: String) = viewModelScope.launch {
        _symbolSearchDataFlow.value = Resource.Loading
        _symbolSearchDataFlow.value = repository.getSymbolSearchResults(query)
    }

    fun updateWatchListSymbols(watchList: WatchList) = viewModelScope.launch {
        localRepository.updateWatchListSymbols(watchList.name, watchList.symbolList)
    }

    fun getWatchList(name: String) = viewModelScope.launch {
        _watchList.value = Resource.Loading
        localRepository.getWatchList(name) {
            _watchList.value = it
        }
    }
}
