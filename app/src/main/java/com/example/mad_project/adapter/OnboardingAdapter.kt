package com.example.mad_project.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.databinding.ItemOnboardingBinding
import com.example.mad_project.model.OnboardingItem

class OnboardingAdapter(
    private val items: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(val binding: ItemOnboardingBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.binding.imgOnboard.setImageResource(item.image)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDesc.text = item.description
    }

    override fun getItemCount() = items.size
}