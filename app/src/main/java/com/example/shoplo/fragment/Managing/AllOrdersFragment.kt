import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.shoplo.adapters.AllOrdersAdapter
import com.example.shoplo.databinding.FragmentOrdersBinding
import com.example.shoplo.util.Resource
import com.example.shoplo.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: OrderViewModel by viewModels()
    private val ordersAdapter = AllOrdersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView
        binding.rvAllOrders.adapter = ordersAdapter

        // Observe the orders from the ViewModel
        lifecycleScope.launch {
            viewModel.order.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading state
                    }
                    is Resource.Success -> {
                        // Update the RecyclerView with the orders
                        ordersAdapter.differ.submitList(resource.data)
                    }
                    is Resource.Error -> {
                        // Show error state
                    }

                    else -> Unit


                }
            }
        }

        // Fetch the orders
        viewModel.getOrders()
    }
}
