package com.karigar.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karigar.app.R
import com.google.android.libraries.places.api.model.AutocompletePrediction

class PlacesAutocompleteAdapter(
    private val onPlaceClick: (AutocompletePrediction) -> Unit
) : RecyclerView.Adapter<PlacesAutocompleteAdapter.PlaceViewHolder>() {

    private var predictions: List<AutocompletePrediction> = emptyList()

    fun setPredictions(newPredictions: List<AutocompletePrediction>) {
        predictions = newPredictions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place_suggestion, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val prediction = predictions[position]
        holder.tvPrimaryText.text = prediction.getPrimaryText(null)
        holder.tvSecondaryText.text = prediction.getSecondaryText(null)
        holder.itemView.setOnClickListener {
            onPlaceClick(prediction)
        }
    }

    override fun getItemCount(): Int = predictions.size

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrimaryText: TextView = itemView.findViewById(R.id.tvPrimaryText)
        val tvSecondaryText: TextView = itemView.findViewById(R.id.tvSecondaryText)
    }
}
