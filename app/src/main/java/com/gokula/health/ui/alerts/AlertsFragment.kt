package com.gokula.health.ui.alerts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gokula.health.database.AppDatabase
import com.gokula.health.ui.vaccination.VaccinationActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlertsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(requireContext()).apply {
            text = "🔔 Vaccination Alerts"
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 24)
        }
        layout.addView(title)

        lifecycleScope.launch {
            val pending = AppDatabase.getInstance(requireContext()).vaccinationDao().getPending()
            requireActivity().runOnUiThread {
                if (pending.isEmpty()) {
                    val empty = TextView(requireContext()).apply {
                        text = "✅ No pending vaccinations!\nAll cattle are up to date."
                        textSize = 16f
                        gravity = android.view.Gravity.CENTER
                        setPadding(0, 64, 0, 0)
                    }
                    layout.addView(empty)
                } else {
                    pending.forEach { v ->
                        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(v.scheduledDate))
                        val item = TextView(requireContext()).apply {
                            text = "💉 ${v.vaccineName}\n🏷️ Tag: ${v.cattleEarTagId}\n📅 $date"
                            textSize = 14f
                            setPadding(24, 24, 24, 24)
                            setBackgroundColor(0xFFFFF3E0.toInt())
                            setOnClickListener {
                                startActivity(
                                    Intent(requireContext(), VaccinationActivity::class.java)
                                        .putExtra("earTag", v.cattleEarTagId)
                                )
                            }
                        }
                        layout.addView(item)
                        val spacer = View(requireContext()).apply { minimumHeight = 12 }
                        layout.addView(spacer)
                    }
                }
            }
        }
        return layout
    }
}