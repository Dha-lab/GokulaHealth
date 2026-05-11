package com.gokula.health.ui.cattle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.ActivityCattleDetailBinding
import com.gokula.health.ui.graph.YieldGraphActivity
import com.gokula.health.ui.milk.MilkDiaryActivity
import com.gokula.health.ui.vaccination.VaccinationActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CattleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCattleDetailBinding
    private lateinit var earTag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCattleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        earTag = intent.getStringExtra("earTag") ?: run { finish(); return }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnMilkDiary.setOnClickListener {
            startActivity(Intent(this, MilkDiaryActivity::class.java).putExtra("earTag", earTag))
        }
        binding.btnVaccination.setOnClickListener {
            startActivity(Intent(this, VaccinationActivity::class.java).putExtra("earTag", earTag))
        }
        binding.btnYieldGraph.setOnClickListener {
            startActivity(Intent(this, YieldGraphActivity::class.java).putExtra("earTag", earTag))
        }
    }

    override fun onResume() {
        super.onResume()
        loadDetails()
    }

    private fun loadDetails() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@CattleDetailActivity)
            val cattle = db.cattleDao().getById(earTag) ?: return@launch

            binding.tvCattleName.text = cattle.name
            binding.tvEarTagBadge.text = "🏷️ Tag: ${cattle.earTagId}"
            binding.tvBreed.text = cattle.breed.ifEmpty { "Not specified" }
            binding.tvAge.text = "${cattle.age} yrs"
            binding.tvWeight.text = if (cattle.weight > 0) "${cattle.weight} kg" else "---"

            if (cattle.photoPath.isNotEmpty()) {
                Glide.with(this@CattleDetailActivity)
                    .load(cattle.photoPath)
                    .centerCrop()
                    .into(binding.ivCattlePhoto)
            }

            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
            val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            val avg = db.milkDao().getMonthlyAverage(earTag, startDate) ?: 0f
            binding.tvMonthlyAvg.text = "%.2f L/day".format(avg)
        }
    }
}