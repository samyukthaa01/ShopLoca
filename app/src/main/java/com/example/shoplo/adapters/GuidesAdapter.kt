package com.example.shoplo.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.data.DataClass

class GuidesAdapter(private val context: Context, private var dataList: List<DataClass>) :
    RecyclerView.Adapter<MyViewHolder>() {

    fun setSearchList(dataSearchList: List<DataClass>) {
        dataList = dataSearchList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guide_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recImage.setImageResource(dataList[position].dataImage)
        holder.recTitle.text = dataList[position].dataTitle
        holder.recDesc.text = dataList[position].dataDesc.toString()
        //holder.recLang.text = dataList[position].dataLang

        holder.recCard.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("Image", dataList[position].dataImage)
                putString("Title", dataList[position].dataTitle)
                putInt("Desc", dataList[position].dataDesc)
            }
            it.findNavController().navigate(R.id.action_guideFragment_to_guideDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
