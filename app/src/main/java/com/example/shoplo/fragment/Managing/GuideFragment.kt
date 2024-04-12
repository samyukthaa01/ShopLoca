package com.example.shoplo.fragment.Managing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.adapters.GuideAdapter
import com.example.shoplo.adapters.GuidesAdapter
import com.example.shoplo.data.DataClass
import java.util.*

class GuideFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var adapter: GuidesAdapter
    private lateinit var androidData: DataClass
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guide, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        val gridLayoutManager = GridLayoutManager(context, 1)
        recyclerView.layoutManager = gridLayoutManager
        dataList = ArrayList()

        androidData = DataClass("Grow Your Business", R.string.camera,  R.drawable.ways_business)
        dataList.add(androidData)

        androidData = DataClass("How to Stand Out", R.string.recyclerview,  R.drawable.stand_out)
        dataList.add(androidData)

        androidData = DataClass("Increase Engagement", R.string.date,  R.drawable.inc_engagement)
        dataList.add(androidData)

        androidData = DataClass("Connect with Customers", R.string.edit, R.drawable.connect)
        dataList.add(androidData)

        androidData = DataClass("Improve Ratings", R.string.rating,  R.drawable.imp_ratings)
        dataList.add(androidData)

        adapter = GuidesAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter

        return view
    }

    private fun searchList(text: String) {
        val dataSearchList = ArrayList<DataClass>()
        for (data in dataList) {
            if (data.dataTitle.toLowerCase(Locale.getDefault()).contains(text.toLowerCase())) {
                dataSearchList.add(data)
            }
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show()
        } else {
            adapter.setSearchList(dataSearchList)
        }
    }
}