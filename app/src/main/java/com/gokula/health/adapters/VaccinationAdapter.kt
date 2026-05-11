package com.gokula.health.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gokula.health.databinding.ItemVaccinationBinding
import com.gokula.health.models.Vaccination
import java.text.SimpleDateFormat
import java.util.*

class VaccinationAdapter(val onToggle: (Vaccination) -> Unit) :
    RecyclerView.Adapter<VaccinationAdapter.VH>() {
    private val items = mutableListOf<Vaccination>()
    fun submit(list: List<Vaccination>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemVaccinationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemVaccinationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val v = items[position]
        holder.binding.apply {
            tvVaccineName.text = v.vaccineName
            tvVaccineDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(v.scheduledDate))
            tvVaccineTag.text = "🏷️ ${v.cattleEarTagId}"
            cbCompleted.setOnCheckedChangeListener(null)
            cbCompleted.isChecked = v.isCompleted
            root.alpha = if (v.isCompleted) 0.6f else 1.0f
            cbCompleted.setOnCheckedChangeListener { _, _ -> onToggle(v) }
        }
    }

    override fun getItemCount() = items.size
}