package com.tastytrade.stock.ui.chart

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.tastytrade.stock.R
import com.tastytrade.stock.databinding.FragmentChartBinding
import com.tastytrade.stock.model.Quote
import com.tastytrade.stock.repository.Resource
import com.tastytrade.stock.ui.BindingFragment
import com.tastytrade.stock.utils.Constants
import com.tastytrade.stock.viewmodel.ChartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class ChartFragment : BindingFragment<FragmentChartBinding>() {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private val viewModel: ChartViewModel by viewModels()
    private lateinit var job: Job

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChartBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quote = arguments?.getParcelable<Quote>("quote")
        job = startRepeatingJob(quote!!.symbol)

        binding.stockSymbol.text = quote.symbol
        binding.bidPrice.text = quote.bidPrice.toString()
        binding.askPrice.text = quote.askPrice.toString()
        binding.lastPrice.text = quote.latestPrice.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chartDataFlow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val xValues = resource.result.map { chartItem -> chartItem.priceDate }

                            //y axis
                            val chartData = mutableListOf<CandleEntry>()
                            // Populating the chart data with ChartItem objects
                            for (chartItem in resource.result) {
                                val candleEntry = CandleEntry(
                                    resource.result.indexOf(chartItem).toFloat(),
                                    chartItem.high.toFloat(),
                                    chartItem.low.toFloat(),
                                    chartItem.open.toFloat(),
                                    chartItem.close.toFloat()
                                )
                                chartData.add(candleEntry)
                            }

                            val dataSet = CandleDataSet(chartData, "Candle Data")
                            dataSet.color = Color.rgb(80, 80, 80)
                            dataSet.shadowColor = Color.DKGRAY
                            dataSet.shadowWidth = 0.7f
                            dataSet.decreasingColor = Color.RED
                            dataSet.decreasingPaintStyle = Paint.Style.FILL
                            dataSet.increasingColor = Color.rgb(122, 242, 84)
                            dataSet.increasingPaintStyle = Paint.Style.FILL

                            binding.candleStickChart.xAxis.textColor = Color.parseColor("#367E35")
                            binding.candleStickChart.axisLeft.textColor = Color.parseColor("#367E35")
                            binding.candleStickChart.axisRight.textColor = Color.parseColor("#367E35")
                            binding.candleStickChart.legend.textColor = Color.parseColor("#367E35")
                            val candleData = CandleData(dataSet)

                            binding.candleStickChart.data = candleData
                            binding.candleStickChart.animateXY(3000, 3000)

                            val xVal = binding.candleStickChart.xAxis
                            xVal.position = XAxis.XAxisPosition.BOTTOM
                            xVal.setDrawGridLines(false)
                        }
                        is Resource.Error -> {
                            Log.d("ChartFragment", resource.exception.message.toString())
                            Toast.makeText(
                                requireContext(),
                                resource.exception.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            Log.d("ChartFragment", "Loading")
                        }
                        else -> {}
                    }
                }
            }
        }

    }

    private fun startRepeatingJob(symbol: String): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                viewModel.getChartData(symbol, Constants.TOKEN)
                delay(5000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
