package com.example.shoplo.fragment.Managing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ListAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoplo.adapters.OrderAdapter

import com.example.shoplo.databinding.FragmentOrdersBinding
import com.example.shoplo.util.Resource
import com.example.shoplo.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: OrderViewModel by viewModels()
    private val orderAdapter by lazy { OrderAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Observe orders from the ViewModel
        lifecycleScope.launchWhenStarted {
            viewModel.order.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Hide progress bar
                        binding.progressbarAllOrders.visibility = View.GONE

                        // Update the RecyclerView with the list of orders
                        orderAdapter.submitList(resource.data)
                        resource.data?.let { updateEmptyStateVisibility(it.isEmpty()) }
                    }
                    is Resource.Loading -> {
                        // Show progress bar
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        // Handle error, e.g., show a message
                        binding.progressbarAllOrders.visibility = View.GONE
                        // You may want to display an error message to the user
                        // using a Toast or other UI element.
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvAllOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun updateEmptyStateVisibility(isEmpty: Boolean) {
        binding.tvEmptyOrders.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}
