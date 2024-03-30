package com.example.shoplo.fragment.Managing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.adapters.FaqAdapter
import com.example.shoplo.data.Faq
import com.example.shoplo.databinding.FragmentFaqBinding
import com.example.shoplo.databinding.FragmentProductdetailviewBinding
import java.util.*

class FaqFragment : Fragment(R.layout.fragment_faq) {
    private lateinit var binding: FragmentFaqBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<Faq>()
    private lateinit var adapter: FaqAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        searchView = binding.searchView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        addDataToList()
        adapter = FaqAdapter(mList)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<Faq>()
            for (i in mList) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(context, "Your message here", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    private fun addDataToList() {
        mList.add(
            Faq(
                "How to add product?",
                R.drawable.productf,
                "Navigate to the product page, click on the + button on bottom of page. It would lead to the add product page."
            )
        )
        mList.add(
            Faq(
                "How to change shop name?",
                R.drawable.shop,
                "Navigate to the profile page and click the edit button to change the shop name. Then, click save to update your changes."
            )
        )
        mList.add(
            Faq(
                "How to choose size of product?",
                R.drawable.tshirt,
                " Specify the size by separting ."
            )
        )
        mList.add(
            Faq(
                "           Should I refund orders that I cancel?",
                R.drawable.refund,
                "Yes, The refund should be done within 3 days of canceling order."
        )

        )
        mList.add(
            Faq(
                "What does the chatbot do?",
                R.drawable.chatbot,
                "The chatbot is your business assistant.Feel free shop related to ask questions to Sam."
            )

        )
    }
}