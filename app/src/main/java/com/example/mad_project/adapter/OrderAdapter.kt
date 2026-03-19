package com.example.mad_project.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.R
import com.example.mad_project.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private val orders: List<Order>,
    private val onItemClick: (Order) -> Unit = {}
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategories: TextView = itemView.findViewById(R.id.tvCategories)
        val tvStatus: TextView     = itemView.findViewById(R.id.tvStatus)
        val tvAddress: TextView    = itemView.findViewById(R.id.tvAddress)
        val tvDetails: TextView    = itemView.findViewById(R.id.tvDetails)
        val tvPhotoCount: TextView = itemView.findViewById(R.id.tvPhotoCount)
        val tvTimestamp: TextView  = itemView.findViewById(R.id.tvTimestamp)
        val tvBidsBadge: TextView  = itemView.findViewById(R.id.tvBidsBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvCategories.text = order.categories.joinToString(" · ")
        holder.tvAddress.text    = order.address
        holder.tvDetails.text    = order.details
        holder.tvPhotoCount.text = if (order.photoCount == 1) "1 photo" else "${order.photoCount} photos"
        holder.tvTimestamp.text  = formatTimestamp(order.timestamp)

        // Bidding UI
        if (order.status == "Pending" && order.bids.isNotEmpty()) {
            holder.tvBidsBadge.visibility = View.VISIBLE
            holder.tvBidsBadge.text = "${order.bids.size} New Bid${if (order.bids.size > 1) "s" else ""}"
        } else {
            holder.tvBidsBadge.visibility = View.GONE
        }

        holder.tvStatus.text = order.status
        val (bgColor, textColor) = getStatusColors(order.status)
        holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bgColor))
        holder.tvStatus.setTextColor(Color.parseColor(textColor))

        // Click listener → open Order Detail
        holder.itemView.setOnClickListener { onItemClick(order) }
    }

    override fun getItemCount(): Int = orders.size

    private fun getStatusColors(status: String): Pair<String, String> = when (status) {
        "Pending"   -> Pair("#FF9800", "#FFFFFF")
        "Accepted"  -> Pair("#2196F3", "#FFFFFF")
        "Assigned"  -> Pair("#9C27B0", "#FFFFFF")
        "Confirmed" -> Pair("#0F2CBD", "#FFFFFF")
        "Completed" -> Pair("#4CAF50", "#FFFFFF")
        "Cancelled" -> Pair("#F44336", "#FFFFFF")
        else        -> Pair("#9E9E9E", "#FFFFFF")
    }

    private fun formatTimestamp(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000L     -> "Just now"
            diff < 3_600_000L  -> "${diff / 60_000} min ago"
            diff < 86_400_000L -> {
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                "Today, ${sdf.format(Date(timestamp))}"
            }
            else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
