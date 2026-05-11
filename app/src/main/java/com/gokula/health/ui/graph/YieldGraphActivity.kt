package com.gokula.health.ui.graph

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.ActivityYieldGraphBinding
import kotlinx.coroutines.launch

class YieldGraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYieldGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYieldGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val earTag = intent.getStringExtra("earTag") ?: run { finish(); return }
        binding.btnBack.setOnClickListener { finish() }
        loadGraph(earTag)
    }

    private fun loadGraph(earTag: String) {
        lifecycleScope.launch {
            val data = AppDatabase.getInstance(this@YieldGraphActivity)
                .milkDao().getLast30Days(earTag)

            if (data.isEmpty()) {
                binding.chart.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
                return@launch
            }

            val entries = data.mapIndexed { i, e -> Entry(i.toFloat(), e.totalYield) }
            val labels = data.map { it.date.takeLast(5) }

            val dataSet = LineDataSet(entries, "Milk Yield (L)").apply {
                color = Color.parseColor("#2E7D32")
                valueTextSize = 9f
                lineWidth = 2.5f
                setCircleColor(Color.parseColor("#2E7D32"))
                circleRadius = 4f
                fillColor = Color.parseColor("#A5D6A7")
                setDrawFilled(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            binding.chart.apply {
                this.data = LineData(dataSet)
                description.text = ""
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    textSize = 9f
                    setDrawGridLines(false)
                }
                axisLeft.textSize = 9f
                axisRight.isEnabled = false
                legend.textSize = 12f
                animateX(800)
                invalidate()
            }

            val avg = data.map { it.totalYield }.average()
            binding.tvAverage.text = "%.1f L".format(avg)
            binding.tvDays.text = "${data.size}"

            val trend = if (data.size >= 3) {
                val recent = data.takeLast(3).map { it.totalYield }.average()
                val older = data.take(3).map { it.totalYield }.average()
                if (recent >= older) "Rising ↑" else "Dropping ↓"
            } else "Stable"
            binding.tvTrend.text = trend
            binding.tvTrend.setTextColor(
                if (trend.contains("Dropping")) Color.RED else Color.parseColor("#2E7D32")
            )
        }
    }
}