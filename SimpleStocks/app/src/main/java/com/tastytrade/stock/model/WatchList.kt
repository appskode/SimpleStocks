package com.tastytrade.stock.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "WatchList")
data class WatchList(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val symbolList: MutableList<String>
)

data class Symbol(
    @SerializedName("quote") val quote: Quote
)

@Parcelize
data class Quote(
    @SerializedName("iexAskPrice") val askPrice: Double,
    @SerializedName("iexBidPrice") val bidPrice: Double,
    @SerializedName("latestPrice") val latestPrice: Double,
    @SerializedName("symbol") val symbol: String
): Parcelable
