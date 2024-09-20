package com.tastytrade.stock.ui.symbolsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tastytrade.stock.databinding.SearchresultItemBinding

class SymbolSearchItemAdapter(private val symbolSearchItems: MutableList<Pair<String, Boolean>>) :
    RecyclerView.Adapter<SymbolSearchItemAdapter.SymbolSearchItemViewHolder>() {
    var onItemChecked: ((String, Boolean) -> Unit)? = null

    inner class SymbolSearchItemViewHolder(private val binding: SearchresultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var onCheck: ((String, Boolean) -> Unit)? = null
        fun bind(item: Pair<String, Boolean>) {
            binding.tvSymbol.text = item.first
            binding.itemCheckbox.isChecked = item.second

            binding.itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onCheck?.invoke(item.first, isChecked)
            }
        }
    }

    override fun getItemCount(): Int {
        if(symbolSearchItems.size != 0 && symbolSearchItems.first().first == "")
            symbolSearchItems.removeAt(0)
        return symbolSearchItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolSearchItemViewHolder {
        val binding =
            SearchresultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SymbolSearchItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SymbolSearchItemViewHolder, position: Int) {
        holder.bind(symbolSearchItems[position])
        holder.onCheck = onItemChecked
    }
}
