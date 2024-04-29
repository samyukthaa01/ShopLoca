import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoplo.R
import com.example.shoplo.data.CurrentUser
import com.example.shoplo.data.Item

class OrderItemAdapter(private val context: Context, private val items: List<Item>, private val users: List<CurrentUser>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_order_detail, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        val user = users[position]

        holder.totalPrice.text = item.totalPrice.toString()
        holder.username.text = user.username
        holder.phone.text = user.phone
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val totalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val username: TextView = itemView.findViewById(R.id.tvFullName)
        val phone: TextView = itemView.findViewById(R.id.tvPhoneNumber)

    }
}
