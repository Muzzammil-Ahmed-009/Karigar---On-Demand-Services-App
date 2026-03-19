package com.example.mad_project

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteWorkersAdapter(
    private var workers: List<FavoriteWorker>,
    private val onUnfavorite: (FavoriteWorker) -> Unit
) : RecyclerView.Adapter<FavoriteWorkersAdapter.WorkerViewHolder>() {

    class WorkerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvWorkerName)
        val tvProfession: TextView = view.findViewById(R.id.tvWorkerProfession)
        val tvRating: TextView = view.findViewById(R.id.tvWorkerRating)
        val ivPhoto: ImageView = view.findViewById(R.id.ivWorkerPhoto)
        val btnCall: ImageView = view.findViewById(R.id.btnCallWorker)
        val btnRemoveFav: ImageView = view.findViewById(R.id.btnRemoveFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_worker, parent, false)
        return WorkerViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        val worker = workers[position]
        holder.tvName.text = worker.name
        holder.tvProfession.text = worker.profession
        holder.tvRating.text = "${worker.rating} ⭐"
        
        holder.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${worker.phone}"))
            holder.itemView.context.startActivity(intent)
        }
        
        holder.btnRemoveFav.setOnClickListener {
            onUnfavorite(worker)
        }
    }

    override fun getItemCount() = workers.size

    fun updateData(newWorkers: List<FavoriteWorker>) {
        workers = newWorkers
        notifyDataSetChanged()
    }
}
