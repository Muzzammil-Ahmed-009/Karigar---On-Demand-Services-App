package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.adapter.OrderAdapter

class PendingOrdersFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var layoutEmpty: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_orders_pending, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvOrders = view.findViewById(R.id.rvPendingOrders)
        layoutEmpty = view.findViewById(R.id.layoutEmptyPending)
        
        layoutEmpty.findViewById<android.widget.ImageView>(R.id.ivEmptyIcon).setImageResource(R.drawable.orders)
        layoutEmpty.findViewById<android.widget.TextView>(R.id.tvEmptyTitle).text = "No active orders"
        layoutEmpty.findViewById<android.widget.TextView>(R.id.tvEmptyDesc).text = "Place an order from Home"
        
        rvOrders.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
        
        // Listen for new bids dropping in
        AppRepository.onBidsUpdatedListener = {
            // Refresh the list when simulated bids arrive
            loadOrders()
        }
    }

    override fun onPause() {
        super.onPause()
        // Clean up memory leak
        AppRepository.onBidsUpdatedListener = null
    }

    private fun loadOrders() {
        val orders = AppRepository.getPendingOrders()
        if (orders.isEmpty()) {
            rvOrders.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            layoutEmpty.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            rvOrders.adapter = OrderAdapter(orders) { order ->
                if (order.status == "Pending" && order.bids.isNotEmpty()) {
                    val intent = Intent(requireContext(), BiddingDetailsActivity::class.java)
                    intent.putExtra("order", order)
                    startActivity(intent)
                } else {
                    val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                    intent.putExtra("order", order)
                    startActivity(intent)
                }
            }
        }
    }
}
