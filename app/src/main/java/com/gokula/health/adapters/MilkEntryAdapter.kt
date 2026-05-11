package com.gokula.health.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gokula.health.databinding.ItemMilkEntryBinding
import com.gokula.health.models.MilkEntry

class MilkEntryAdapter : RecyclerView.Adapter<MilkEntryAdapter.VH>() {
    private val items = mutableListOf<MilkEntry>()
    fun submit(list: List<MilkEntry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemMilkEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMilkEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val entry = items[position]
        holder.binding.apply {
            try {
                val parts = entry.date.split("-")
                tvDayNumber.text = parts[2]
                val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
                tvMonth.text = months.getOrElse(parts[1].toInt() - 1) { "?" }
            } catch (e: Exception) {
                tvDayNumber.text = "?"
                tvMonth.text = "?"
            }
            tvDate.text = entry.date
            tvMorning.text = "🌅 ${entry.morningYield}L"
            tvEvening.text = "🌙 ${entry.eveningYield}L"
            tvTotal.text = "%.1f L".format(entry.totalYield)
        }
    }

    override fun getItemCount() = items.size
}