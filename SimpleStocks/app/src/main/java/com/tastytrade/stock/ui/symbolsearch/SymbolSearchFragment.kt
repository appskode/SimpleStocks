package com.tastytrade.stock.ui.symbolsearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.tastytrade.stock.databinding.FragmentSymbolSearchBinding
import com.tastytrade.stock.model.WatchList
import com.tastytrade.stock.repository.Resource
import com.tastytrade.stock.ui.BindingFragment
import com.tastytrade.stock.viewmodel.SymbolSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SymbolSearchFragment : BindingFragment<FragmentSymbolSearchBinding>() {

    companion object {
        fun newInstance() = SymbolSearchFragment()
    }

    private val viewModel: SymbolSearchViewModel by viewModels()
    private lateinit var currentWatchList: WatchList
    private lateinit var adapter: SymbolSearchItemAdapter
    private lateinit var symbolList: MutableList<Pair<String, Boolean>>

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSymbolSearchBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentWatchList = viewModel.getCurrentWatchList()!!

        val searchRunnable = Runnable {
            viewModel.getSymbolSearchData(binding.searchView.query.toString())
        }

        symbolList = currentWatchList.symbolList.map {
            Pair(it, true)
        } as MutableList<Pair<String, Boolean>>
        setAdapter(symbolList)


        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.getSymbolSearchData(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    symbolList = currentWatchList.symbolList.map {
                        Pair(it, true)
                    } as MutableList<Pair<String, Boolean>>
                    setAdapter(symbolList)
                    return true
                }
                binding.searchView.removeCallbacks(searchRunnable)
                binding.searchView.postDelayed(searchRunnable, 500)
                return true
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.symbolSearchDataFlow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            symbolList = resource.result.data.items.map {
                                Pair(it.symbol, false)
                            }.filter { !currentWatchList.symbolList.contains(it.first) } as MutableList<Pair<String, Boolean>>

                            val checkedList = currentWatchList.symbolList.map {
                                Pair(it, true)
                            }

                            symbolList = (checkedList + symbolList) as MutableList<Pair<String, Boolean>>

                            setAdapter(symbolList)
                            Log.d("SymbolSearchFragmentAfter", symbolList.toString())
                            Log.d("SymbolSearchFragment", currentWatchList.symbolList.toString())
                        }
                        is Resource.Error -> {
                            Log.d("SymbolSearchFragment", resource.exception.message.toString())
                        }
                        is Resource.Loading -> {
                            Log.d("SymbolSearchFragment", "Loading")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setAdapter(symbolList: MutableList<Pair<String, Boolean>>) {
        adapter = SymbolSearchItemAdapter(symbolList)
        binding.searchResults.adapter = adapter
        adapter.onItemChecked = { symbol, isChecked ->
            if (isChecked) {
                currentWatchList.symbolList.add(symbol)
                Log.d("SymbolSearchFragment", currentWatchList.symbolList.toString())
            } else {
                currentWatchList.symbolList.remove(symbol)
                Log.d("SymbolSearchFragment", currentWatchList.symbolList.toString())
            }
            viewModel.updateWatchListSymbols(currentWatchList)
        }
    }
}
