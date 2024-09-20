package com.tastytrade.stock.ui.watchlist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.tastytrade.stock.R
import com.tastytrade.stock.databinding.FragmentWatchListBinding
import com.tastytrade.stock.model.WatchList
import com.tastytrade.stock.repository.Resource
import com.tastytrade.stock.ui.BindingFragment
import com.tastytrade.stock.utils.Constants.TOKEN
import com.tastytrade.stock.viewmodel.WatchListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class WatchListFragment : BindingFragment<FragmentWatchListBinding>() {

    companion object {
        fun newInstance() = WatchListFragment()
    }

    private val viewModel: WatchListViewModel by viewModels()
    private val types = "quote"
    private lateinit var adapter: WatchListItemAdapter
    private lateinit var job: Job

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentWatchListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getCurrentWatchList() == null) {
            viewModel.getFirstWatchList()
        } else {
            viewModel.getWatchList(viewModel.getCurrentWatchList()!!.name)
        }

        binding.deleteBtn.setOnClickListener {
            showDeleteDialog()
        }

        binding.createList.setOnClickListener {
            showDialog("")
        }

        binding.editList.setOnClickListener {
            viewModel.getCurrentWatchList()?.let { it1 -> showDialog(it1.name) }
        }

        binding.tvSwitch.setOnClickListener {
            viewModel.getAllWatchList()
            if (binding.listView.visibility == View.GONE) {
                binding.listView.visibility = View.VISIBLE
            } else {
                binding.listView.visibility = View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allWatchList.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.listView.adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.switch_item,
                                resource.result
                            )

                            binding.listView.setOnItemClickListener { _, _, position, _ ->
                                job.cancel()
                                binding.rvWatchLists.adapter = null
                                val watchList = resource.result[position]
                                viewModel.getWatchList(watchList)
                                binding.listView.visibility = View.GONE
                            }
                        }
                        is Resource.Error -> {
                            Log.d("WatchListFragment", resource.exception.message.toString())
                            Toast.makeText(
                                requireContext(),
                                resource.exception.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            Log.d("WatchListFragment", "Loading")
                        }
                        else -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.watchList.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.rvWatchLists.adapter = null
                            if (resource.result == null) {
                                viewModel.addWatchList(
                                    WatchList(
                                        "My First List",
                                        mutableListOf("GOOGL", "AAPL", "MSFT")
                                    )
                                )
                                viewModel.getWatchList("My First List")
                                return@collect
                            }
                            val symbols = resource.result.symbolList.joinToString(",")
                            binding.name.text = resource.result.name
                            viewModel.setCurrentWatchList(resource.result)
                            job = startRepeatingJob(symbols)
                        }
                        is Resource.Error -> {
                            Log.d("WatchListFragment", resource.exception.message.toString())
                            Toast.makeText(
                                requireContext(),
                                resource.exception.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            Log.d("WatchListFragment", "Loading")
                        }
                        else -> {}
                    }
                }
            }
        }

        binding.addSymbol.setOnClickListener {
            job.cancel()
            binding.rvWatchLists.adapter = null
            findNavController().navigate(R.id.symbolSearchFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.symbolsDataFlow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            adapter = WatchListItemAdapter(resource.result.values.toList())
                            binding.rvWatchLists.adapter = adapter
                            adapter.itemClickListener = { quote ->
                                findNavController().navigate(
                                    R.id.action_watchListFragment_to_chartFragment,
                                    Bundle().apply {
                                        putParcelable("quote", quote)

                                    })
                            }
                        }
                        is Resource.Error -> {
                            Log.d("WatchListFragment", resource.exception.message.toString())
                            Toast.makeText(
                                requireContext(),
                                resource.exception.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            Log.d("WatchListFragment", "Loading")
                        }
                        else -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateName.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.name.text = resource.result
                            viewModel.getCurrentWatchList()
                                ?.let { WatchList(resource.result, it.symbolList) }
                                ?.let { viewModel.setCurrentWatchList(it) }
                        }
                        is Resource.Error -> {
                            Log.d("WatchListFragment", resource.exception.message.toString())
                            Toast.makeText(
                                requireContext(),
                                resource.exception.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            Log.d("WatchListFragment", "Loading")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showDialog(name: String) {
        val dial = Dialog(this.requireContext())
        dial.setContentView(R.layout.dialog)
        dial.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dial.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val txtName = dial.findViewById<EditText>(R.id.txtName)
        val btnSave = dial.findViewById<Button>(R.id.btnSave)
        if (name.isNotEmpty()) {
            txtName.setText(name)
        }
        btnSave?.setOnClickListener {
            if (txtName.text.toString().isNotEmpty()) {
                val newName = txtName.text.toString()
                if (name.isEmpty()) {
                    viewModel.addWatchList(
                        WatchList(newName, mutableListOf("MSFT"))
                    )
                } else {
                    viewModel.updateWatchListName(name, newName)
                }
                dial.dismiss()
            } else {
                txtName.error = "Name cannot be empty"
            }
        }
        dial.show()
    }

    private fun startRepeatingJob(symbols: String): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            if (symbols.isEmpty()) {
                return@launch
            }
            while (true) {
                viewModel.getWatchListData(TOKEN, symbols, types)
                delay(5000)
            }
        }
    }

    private fun showDeleteDialog(){
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Delete Item")
        alertDialog.setMessage("Are you sure you want to delete this?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            job.cancel()
            viewModel.deleteWatchList(viewModel.getCurrentWatchList()!!.name).invokeOnCompletion {
                viewModel.getFirstWatchList()
            }
        }
        alertDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
