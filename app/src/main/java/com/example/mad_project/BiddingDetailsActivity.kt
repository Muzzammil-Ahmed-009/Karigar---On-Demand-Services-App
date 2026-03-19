package com.example.mad_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.adapter.BidAdapter
import com.example.mad_project.model.Order
import com.example.mad_project.model.WorkerBid
import java.util.UUID
import com.example.mad_project.model.AppNotification

class BiddingDetailsActivity : AppCompatActivity() {

    private lateinit var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bidding_details)

        // Retrieve order details from intent
        order = intent.getParcelableExtra("order") ?: return finish()

        // Handle Top Bar
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Setup Summary Card
        findViewById<TextView>(R.id.tvSummaryCategory).text = order.categories.joinToString(" · ")
        findViewById<TextView>(R.id.tvSummaryAddress).text = order.address
        findViewById<TextView>(R.id.tvSummaryFare).text = "Rs. ${order.offeredFare.ifEmpty { "0" }}"

        // Setup RecyclerView
        val rvBids = findViewById<RecyclerView>(R.id.rvBids)
        rvBids.layoutManager = LinearLayoutManager(this)
        rvBids.adapter = BidAdapter(order.bids) { acceptedBid ->
            acceptBid(acceptedBid)
        }
    }

    private fun acceptBid(bid: WorkerBid) {
        // Mark bid as accepted
        bid.isAccepted = true

        // Update Order in Repository
        val repoOrder = AppRepository.getOrderById(order.id)
        if (repoOrder != null) {
            // "In-Drive" style: order moves to Assigned state immediately since worker agreed
            repoOrder.status = "Assigned"
            
            // Set the assigned worker info from the winning bid
            var assignedRepoOrder = repoOrder.copy(
                assignedWorkerName = bid.workerName,
                assignedWorkerRating = bid.workerRating,
                assignedWorkerExperience = "${bid.jobsCompleted} jobs completed",
                status = "Assigned"
            )
            
            // Note: because AppRepository.orders list contains var order, we must swap the item
            val index = AppRepository.orders.indexOf(repoOrder)
            if (index != -1) {
                AppRepository.orders[index] = assignedRepoOrder
            }

            // Create notification for assignment
            AppRepository.notifications.add(0, AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Worker Assigned! 👨‍🔧",
                message = "${bid.workerName} has been assigned to your order. ETA: ${bid.etaString}.",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                type = "assigned"
            ))

            Toast.makeText(this, "Bid Accepted! ${bid.workerName} is on the way.", Toast.LENGTH_LONG).show()
        }

        // Finish activity to return back to Orders list
        finish()
    }
}
