package com.tastytrade.stock.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tastytrade.stock.model.ChartResponseData
import com.tastytrade.stock.repository.Resource
import com.tastytrade.stock.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: StocksRepository
) : ViewModel() {
    private val _chartDataFlow = MutableStateFlow<Resource<ChartResponseData>?>(null)
    val chartDataFlow = _chartDataFlow.asStateFlow()

    fun getChartData(symbol: String, token: String) =
        viewModelScope.launch {
            _chartDataFlow.value = Resource.Loading
            _chartDataFlow.value = repository.getChartData(symbol, token)
        }
}
