package com.example.mad_project

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad_project.databinding.ActivityFavoriteWorkersBinding

class FavoriteWorkersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteWorkersBinding
    private lateinit var adapter: FavoriteWorkersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteWorkersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        adapter = FavoriteWorkersAdapter(AppRepository.favoriteWorkers.toList()) { worker ->
            AppRepository.toggleFavoriteWorker(worker)
            refreshList()
        }

        binding.rvFavoriteWorkers.layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteWorkers.adapter = adapter
        
        updateEmptyState()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val list = AppRepository.favoriteWorkers.toList()
        adapter.updateData(list)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        val list = AppRepository.favoriteWorkers
        if (list.isEmpty()) {
            binding.rvFavoriteWorkers.visibility = View.GONE
            binding.layoutEmptyState.root.visibility = View.VISIBLE
            
            binding.layoutEmptyState.ivEmptyIcon.setImageResource(R.drawable.ic_heart_outline)
            binding.layoutEmptyState.ivEmptyIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF3B30"))
            binding.layoutEmptyState.tvEmptyTitle.text = "No Favorite Workers"
            binding.layoutEmptyState.tvEmptyDesc.text = "Workers you save will appear here for easy access."
        } else {
            binding.rvFavoriteWorkers.visibility = View.VISIBLE
            binding.layoutEmptyState.root.visibility = View.GONE
        }
    }
}
