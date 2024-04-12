package com.example.shoplo.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.data.DataClass
import com.example.shoplo.fragment.Managing.GuideDetailsFragment


class GuideAdapter(private val context: Context, private val fragmentManager: FragmentManager, private var dataList: List<DataClass>) :
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
       // holder.recLang.text = dataList[position].dataLang

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

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val recImage: ImageView = itemView.findViewById(R.id.recImage)
    val recTitle: TextView = itemView.findViewById(R.id.recTitle)
    val recDesc: TextView = itemView.findViewById(R.id.recDesc)
    //val recLang: TextView = itemView.findViewById(R.id.recLang)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
}
