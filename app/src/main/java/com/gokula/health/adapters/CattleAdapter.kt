package com.gokula.health.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gokula.health.databinding.ItemCattleBinding
import com.gokula.health.models.Cattle

class CattleAdapter(val onClick: (Cattle) -> Unit) : RecyclerView.Adapter<CattleAdapter.VH>() {
    private val items = mutableListOf<Cattle>()
    fun submit(list: List<Cattle>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemCattleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCattleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cattle = items[position]
        holder.binding.apply {
            tvCattleName.text = cattle.name
            tvEarTag.text = "🏷️ ${cattle.earTagId}"
            tvBreed.text = cattle.breed.ifEmpty { "Unknown breed" }
            tvHealthStatus.text = "● ${cattle.healthStatus}"
            if (cattle.photoPath.isNotEmpty()) {
                Glide.with(imgCattle)
                    .load(cattle.photoPath)
                    .centerCrop()
                    .into(imgCattle)
            } else {
                imgCattle.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            root.setOnClickListener { onClick(cattle) }
        }
    }

    override fun getItemCount() = items.size
}