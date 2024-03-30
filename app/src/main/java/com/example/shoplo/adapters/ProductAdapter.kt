import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoplo.R
import com.example.shoplo.data.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProduct: ImageView = itemView.findViewById(R.id.Img)
        val textViewProductName: TextView = itemView.findViewById(R.id.pname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        // Assuming productImages is a list of image URLs in your Product class
        val imageUrl = product.productImages.firstOrNull() // You may need to handle multiple images here

        // Use Picasso to load the image into the ImageView
        if (imageUrl != null) {
            Picasso.get()
                .load(imageUrl)
                .error(R.drawable.icon_background) // Set a placeholder for image load errors
                .into(holder.imageViewProduct)
        } else {
            // Handle the case where there is no image URL
            // You may want to set a placeholder image or handle this case differently
            holder.imageViewProduct.setImageResource(R.drawable.icon_background)
        }
        // Set product name to the TextView
        holder.textViewProductName.text = product.productName

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)

        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    var onClick: ((Product) -> Unit)? = null
}
