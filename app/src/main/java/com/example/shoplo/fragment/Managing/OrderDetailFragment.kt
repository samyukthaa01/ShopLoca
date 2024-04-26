package com.example.shoplo.fragment.Managing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.adapters.OrderItemAdapter
import com.example.shoplo.data.OrderStatus
import com.example.shoplo.data.getOrderStatus
import com.example.shoplo.databinding.FragmentOrderDetailBinding
import com.example.shoplo.util.VerticalItemDecoration

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var orderItemAdapter: OrderItemAdapter
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
        Log.d("OrderDetailFragment", "Number of products: ${order.products.size}")

        // Loop through and log each product
        order.products.forEachIndexed { index, product ->
            Log.d("OrderDetailFragment", "Product $index: $product")
        }

        // Initialize the OrderItemAdapter with the list of items
        orderItemAdapter = OrderItemAdapter(requireContext(), order.products)

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

            tvFullName.text = order.address.username
            // tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = "$ ${order.totalPrice}"
        }
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
