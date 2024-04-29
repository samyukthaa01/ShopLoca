package com.example.shoplo.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoplo.R
import com.example.shoplo.data.Item
import de.hdodenhof.circleimageview.CircleImageView

class OrderRecyclerAdapter(private val context: Context, private val items: List<Item>) :
    RecyclerView.Adapter<OrderRecyclerAdapter.BillingItemViewHolder>() {

    inner class BillingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val totalQuantity: TextView = itemView.findViewById(R.id.tvBillingProductQuantity)
        val productName: TextView = itemView.findViewById(R.id.tvProductCartName)
        val productPrice: TextView = itemView.findViewById(R.id.tvProductCartPrice)
        val productImage: ImageView = itemView.findViewById(R.id.imageCartProduct)
        val selectedColor: TextView = itemView.findViewById(R.id.tvCartProductSize)
        val selectedSize: CircleImageView = itemView.findViewById(R.id.imageCartProductColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_products_rv_item, parent, false)
        return BillingItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillingItemViewHolder, position: Int) {
        val item = items[position]

        holder.productName.text = item.productName
        holder.productPrice.text = item.productPrice.toString()
        holder.totalQuantity.text = item.totalQuantity.toString()
        holder.selectedColor.text = if (item.selectedColor == "none") "-" else item.selectedColor
                    if (item.selectedSize == "0") {
                        holder.selectedSize.setColorFilter(Color.parseColor("#FFFFFF")) // Set to black or any other color you prefer
        } else {
            holder.selectedSize.setColorFilter(Color.parseColor(item.selectedSize))
        }

        // Load the first image by default
        if (item.productImages.isNotEmpty()) {
            Glide.with(context)
                .load(item.productImages[0]) // Load the first image URL
                .placeholder(R.drawable.placeholder)
                .into(holder.productImage)
        } else {
            // Handle case when there are no images
            holder.productImage.setImageResource(R.drawable.placeholder)
        }
    }



    override fun getItemCount(): Int {
        return items.size
    }
}