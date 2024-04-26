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
import java.lang.String
import kotlin.Int


class ItemAdapter(private val context: Context, private var itemIds: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.order_items, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        // Reset views
        holder.product_name.text = ""
        holder.product_price.text = ""
        holder.total_quantity.text = ""
        holder.total_price.text = ""
        holder.product_image.setImageResource(0) // or set a placeholder image

        // Bind new data to views
        val item: Item = itemIds[position]
        holder.product_name.text = item.productName
        holder.product_price.text = item.productPrice.toString()
        holder.total_quantity.text = item.totalQuantity.toString()
        holder.total_price.text = item.totalPrice.toString()
        // Load image using Glide
        Glide.with(context)
            .load(item.productImage)
            .placeholder(R.drawable.placeholder)
            .into(holder.product_image)
    }

    override fun getItemCount(): Int {
        return itemIds.size
    }
    fun updateItems(newItems: List<Item>) {
        this.itemIds = newItems
        notifyDataSetChanged()
    }
    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var total_quantity: TextView
        var product_name: TextView
        var product_price: TextView
        var total_price: TextView
        var product_image: ImageView

        init {
            total_quantity = itemView.findViewById<TextView>(R.id.total_quantity)
            product_name = itemView.findViewById<TextView>(R.id.product_name)
            product_price = itemView.findViewById<TextView>(R.id.product_price)
            total_price = itemView.findViewById<TextView>(R.id.total_price)
            product_image = itemView.findViewById<ImageView>(R.id.product_image)
        }
    }
}
