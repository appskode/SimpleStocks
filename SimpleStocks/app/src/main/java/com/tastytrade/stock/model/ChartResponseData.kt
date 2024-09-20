package com.tastytrade.stock.model

import com.google.gson.annotations.SerializedName

class ChartResponseData : ArrayList<ChartResponseDataItem>()

data class ChartResponseDataItem(
    @SerializedName("change")
    var change: Double,
    @SerializedName("changeOverTime")
    var changeOverTime: Double,
    @SerializedName("changePercent")
    var changePercent: Double,
    @SerializedName("close")
    var close: Double,
    @SerializedName("date")
    var date: String,
    @SerializedName("fClose")
    var fClose: Double,
    @SerializedName("fHigh")
    var fHigh: Double,
    @SerializedName("fLow")
    var fLow: Double,
    @SerializedName("fOpen")
    var fOpen: Double,
    @SerializedName("fVolume")
    var fVolume: Int,
    @SerializedName("high")
    var high: Double,
    @SerializedName("id")
    var id: String,
    @SerializedName("key")
    var key: String,
    @SerializedName("label")
    var label: String,
    @SerializedName("low")
    var low: Double,
    @SerializedName("marketChangeOverTime")
    var marketChangeOverTime: Double,
    @SerializedName("open")
    var `open`: Double,
    @SerializedName("priceDate")
    var priceDate: String,
    @SerializedName("subkey")
    var subkey: String,
    @SerializedName("symbol")
    var symbol: String,
    @SerializedName("uClose")
    var uClose: Double,
    @SerializedName("uHigh")
    var uHigh: Double,
    @SerializedName("uLow")
    var uLow: Double,
    @SerializedName("uOpen")
    var uOpen: Double,
    @SerializedName("uVolume")
    var uVolume: Int,
    @SerializedName("updated")
    var updated: Long,
    @SerializedName("volume")
    var volume: Int
)