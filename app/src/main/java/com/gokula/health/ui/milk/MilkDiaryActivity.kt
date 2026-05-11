package com.gokula.health.ui.milk

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokula.health.adapters.MilkEntryAdapter
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.ActivityMilkDiaryBinding
import com.gokula.health.models.MilkEntry
import com.gokula.health.utils.FirebaseSync
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MilkDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMilkDiaryBinding
    private lateinit var adapter: MilkEntryAdapter
    private var earTag: String = ""
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMilkDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        earTag = intent.getStringExtra("earTag") ?: ""
        adapter = MilkEntryAdapter()
        binding.recyclerMilk.layoutManager = LinearLayoutManager(this)
        binding.recyclerMilk.adapter = adapter

        val today = sdf.format(Date())
        binding.etDate.setText(today)

        binding.btnBack.setOnClickListener { finish() }

        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnAddEntry.setOnClickListener { addEntry() }
        loadEntries()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d)
            binding.etDate.setText(sdf.format(cal.time))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun addEntry() {
        val date = binding.etDate.text.toString().trim()
        val morning = binding.etMorning.text.toString().toFloatOrNull() ?: 0f
        val evening = binding.etEvening.text.toString().toFloatOrNull() ?: 0f

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }
        if (morning == 0f && evening == 0f) {
            Toast.makeText(this, "Enter at least morning or evening yield", Toast.LENGTH_SHORT).show()
            return
        }

        // If no specific cattle, prompt user; for now use global if earTag is empty
        if (earTag.isEmpty()) {
            Toast.makeText(this, "Open from cattle profile to track per-cattle yield", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = MilkEntry(
            cattleEarTagId = earTag,
            date = date,
            morningYield = morning,
            eveningYield = evening
        )
        lifecycleScope.launch {
            val id = AppDatabase.getInstance(this@MilkDiaryActivity).milkDao().insert(entry)
            FirebaseSync.uploadMilkEntry(entry.copy(id = id))
            runOnUiThread {
                binding.etMorning.text?.clear()
                binding.etEvening.text?.clear()
                Toast.makeText(
                    this@MilkDiaryActivity,
                    "✅ Saved! Total: ${morning + evening}L",
                    Toast.LENGTH_SHORT
                ).show()
                loadEntries()
            }
        }
    }

    private fun loadEntries() {
        if (earTag.isEmpty()) return
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@MilkDiaryActivity)
            val list = db.milkDao().getByCattle(earTag)
            adapter.submit(list)
            binding.tvTotalEntries.text = list.size.toString()

            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
            val startDate = sdf.format(cal.time)
            val avg = db.milkDao().getMonthlyAverage(earTag, startDate) ?: 0f
            binding.tvMonthlyAvg.text = "%.2f L/day".format(avg)
        }
    }
}