package com.gokula.health.ui.vaccination

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokula.health.adapters.VaccinationAdapter
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.ActivityVaccinationBinding
import com.gokula.health.models.Vaccination
import com.gokula.health.utils.AlarmHelper
import com.gokula.health.utils.FirebaseSync
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class VaccinationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVaccinationBinding
    private lateinit var adapter: VaccinationAdapter
    private var earTag: String = ""
    private var selectedDateMillis: Long = 0L
    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        earTag = intent.getStringExtra("earTag") ?: ""
        adapter = VaccinationAdapter(
            onToggle = { v ->
                lifecycleScope.launch {
                    val updated = v.copy(isCompleted = !v.isCompleted)
                    AppDatabase.getInstance(this@VaccinationActivity).vaccinationDao().update(updated)
                    FirebaseSync.syncVaccinationUpdate(updated)
                    if (updated.isCompleted) AlarmHelper.cancelReminder(this@VaccinationActivity, v.id)
                    loadList()
                }
            }
        )
        binding.recyclerVaccination.layoutManager = LinearLayoutManager(this)
        binding.recyclerVaccination.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPickDate.setOnClickListener { showDatePicker() }
        binding.btnSchedule.setOnClickListener { scheduleVaccination() }

        // Quick chips
        binding.chipFMD.setOnClickListener { binding.etVaccineName.setText("FMD") }
        binding.chipBQ.setOnClickListener { binding.etVaccineName.setText("BQ") }
        binding.chipHS.setOnClickListener { binding.etVaccineName.setText("HS") }
        binding.chipBrucellosis.setOnClickListener { binding.etVaccineName.setText("Brucellosis") }

        loadList()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d, 9, 0, 0)
            selectedDateMillis = cal.timeInMillis
            binding.tvSelectedDate.text = "📅 ${sdf.format(cal.time)}"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun scheduleVaccination() {
        val name = binding.etVaccineName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tilVaccineName.error = "Vaccine name required"
            return
        }
        binding.tilVaccineName.error = null
        if (selectedDateMillis == 0L) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }
        if (earTag.isEmpty()) {
            Toast.makeText(this, "Open from a cattle profile to schedule per-cattle vaccination", Toast.LENGTH_SHORT).show()
            return
        }

        val vaccination = Vaccination(
            cattleEarTagId = earTag,
            vaccineName = name,
            scheduledDate = selectedDateMillis,
            notes = binding.etNotes.text.toString().trim()
        )
        lifecycleScope.launch {
            val id = AppDatabase.getInstance(this@VaccinationActivity)
                .vaccinationDao().insert(vaccination)
            val saved = vaccination.copy(id = id)
            AlarmHelper.scheduleReminder(this@VaccinationActivity, saved)
            FirebaseSync.uploadVaccination(saved)
            runOnUiThread {
                Toast.makeText(this@VaccinationActivity, "✅ Vaccination scheduled! 🔔 Reminder set", Toast.LENGTH_SHORT).show()
                binding.etVaccineName.text?.clear()
                binding.etNotes.text?.clear()
                binding.tvSelectedDate.text = "📅 Select date..."
                selectedDateMillis = 0L
                loadList()
            }
        }
    }

    private fun loadList() {
        if (earTag.isEmpty()) return
        lifecycleScope.launch {
            val list = AppDatabase.getInstance(this@VaccinationActivity)
                .vaccinationDao().getByCattle(earTag)
            adapter.submit(list)
        }
    }
}