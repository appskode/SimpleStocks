package com.tastytrade.stock.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tastytrade.stock.databinding.WatchlistItemBinding
import com.tastytrade.stock.model.Quote
import com.tastytrade.stock.model.Symbol

class WatchListItemAdapter(private val watchListItem: List<Symbol>) :
    RecyclerView.Adapter<WatchListItemAdapter.WatchListViewHolder>() {

    var itemClickListener: ((symbol: Quote) -> Unit)? = null

    inner class WatchListViewHolder(private val binding: WatchlistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var itemClick: ((symbol: Quote) -> Unit)? = null
        fun bind(item: Symbol) {
            item.quote.let {
                binding.tvSymbol.text = it.symbol
                binding.tvAskPrice.text = it.askPrice.toString()
                binding.tvBidPrice.text = it.bidPrice.toString()
                binding.tvLastPrice.text = it.latestPrice.toString()
            }
            binding.root.setOnClickListener {
                itemClick?.invoke(item.quote)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListViewHolder {
        val binding =
            WatchlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WatchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WatchListViewHolder, position: Int) {
        holder.bind(watchListItem[position])
        holder.itemClick = itemClickListener
    }

    override fun getItemCount() = watchListItem.size

}
