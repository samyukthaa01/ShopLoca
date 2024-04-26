package com.example.shoplo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoplo.R
import com.example.shoplo.data.Item

class OrderItemAdapter(private val context: Context, private val items: List<Item>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_products_rv_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]

        holder.productName.text = item.productsName
        holder.productPrice.text = item.productPrice.toString()
        holder.totalQuantity.text = item.totalQuantity.toString()
        holder.totalPrice.text = item.totalPrice.toString()

        Glide.with(context)
            .load(item.productImage)
            .placeholder(R.drawable.placeholder)
            .into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val totalQuantity: TextView = itemView.findViewById(R.id.total_quantity)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val totalPrice: TextView = itemView.findViewById(R.id.total_price)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
    }
}
