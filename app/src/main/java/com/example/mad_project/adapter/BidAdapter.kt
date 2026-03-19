package com.example.mad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.R
import com.example.mad_project.model.WorkerBid

class BidAdapter(
    private val bids: List<WorkerBid>,
    private val onAcceptClick: (WorkerBid) -> Unit
) : RecyclerView.Adapter<BidAdapter.BidViewHolder>() {

    inner class BidViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivWorkerImage: ImageView = itemView.findViewById(R.id.ivWorkerImage)
        val tvWorkerName: TextView = itemView.findViewById(R.id.tvWorkerName)
        val tvWorkerRating: TextView = itemView.findViewById(R.id.tvWorkerRating)
        val tvJobsCompleted: TextView = itemView.findViewById(R.id.tvJobsCompleted)
        val tvEta: TextView = itemView.findViewById(R.id.tvEta)
        val tvBidAmount: TextView = itemView.findViewById(R.id.tvBidAmount)
        val btnAcceptBid: Button = itemView.findViewById(R.id.btnAcceptBid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bid, parent, false)
        return BidViewHolder(view)
    }

    override fun onBindViewHolder(holder: BidViewHolder, position: Int) {
        val bid = bids[position]

        holder.ivWorkerImage.setImageResource(bid.workerImageResId)
        holder.tvWorkerName.text = bid.workerName
        holder.tvWorkerRating.text = bid.workerRating.toString()
        holder.tvJobsCompleted.text = " (${bid.jobsCompleted} jobs)"
        holder.tvEta.text = bid.etaString
        holder.tvBidAmount.text = "Rs ${bid.bidAmount}"

        holder.btnAcceptBid.setOnClickListener {
            onAcceptClick(bid)
        }
    }

    override fun getItemCount(): Int = bids.size
}
