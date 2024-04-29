package com.example.shoplo.fragment.Managing


import OrderItemAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R

import com.example.shoplo.adapters.OrderRecyclerAdapter
import com.example.shoplo.data.OrderStatus
import com.example.shoplo.data.getOrderStatus
import com.example.shoplo.databinding.FragmentOrderDetailBinding
import com.example.shoplo.databinding.FragmentOrdersBinding
import com.example.shoplo.util.VerticalItemDecoration
import android.graphics.Color
import android.util.Log
import de.hdodenhof.circleimageview.CircleImageView
//import com.example.kelineyt.util.hideBottomNavigationView

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var billingItemAdapter: OrderRecyclerAdapter
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        // Logging the order ID
        Log.d("OrderDetailFragment", "Order ID: ${order.orderId}")

        // Logging the number of products
        Log.d("OrderDetailFragment", "Number of items: ${order.items.size}")

        // Loop through and log each product
        order.items.forEachIndexed { index, item ->
            Log.d("OrderDetailFragment", "Item $index: $item")
        }
        //hideBottomNavigationView()
        // Initialize the OrderItemAdapter with an empty list initially
        orderItemAdapter = OrderItemAdapter(requireContext(), order.items, order.users)

        // Initialize the BillingItemAdapter with the items from the order
        billingItemAdapter = OrderRecyclerAdapter(requireContext(), order.items)

        setupOrderRv()

        binding.apply {

            tvOrderId.text = "Order #${order.orderId}"


            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }


            var totalPrice = 0.0
            order.items.forEach { item ->
                totalPrice += item.totalPrice.toDouble() // Convert item.totalPrice to Double before adding
            }

            tvTotalPrice.text = "RM $totalPrice"

            if (order.users.isNotEmpty()) {
                // Assuming you only want to display the username and phone of the first user
                val user = order.users[0]
                tvFullName.text = user.username ?: ""
                tvPhoneNumber.text = user.phone ?: ""
            }
        }

        //billingProductsAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingItemAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

}