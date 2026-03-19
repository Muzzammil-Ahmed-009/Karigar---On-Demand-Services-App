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

class HistoryOrdersFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var layoutEmpty: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_orders_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvOrders = view.findViewById(R.id.rvHistoryOrders)
        layoutEmpty = view.findViewById(R.id.layoutEmptyHistory)
        rvOrders.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun loadOrders() {
        val orders = AppRepository.getHistoryOrders()
        if (orders.isEmpty()) {
            rvOrders.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            layoutEmpty.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            rvOrders.adapter = OrderAdapter(orders) { order ->
                val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                intent.putExtra("order", order)
                startActivity(intent)
            }
        }
    }
}
