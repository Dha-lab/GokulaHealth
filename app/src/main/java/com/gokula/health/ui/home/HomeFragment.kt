package com.gokula.health.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.FragmentHomeBinding
import com.gokula.health.models.GroqMessage
import com.gokula.health.ui.cattle.AddCattleActivity
import com.gokula.health.ui.milk.MilkDiaryActivity
import com.gokula.health.ui.scan.TagScannerActivity
import com.gokula.health.ui.vaccination.VaccinationActivity
import com.gokula.health.utils.GroqService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadStats()
    }

    private fun setupUI() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning 👋"
            hour < 17 -> "Good Afternoon 👋"
            else -> "Good Evening 👋"
        }
        binding.tvGreeting.text = greeting

        val email = FirebaseAuth.getInstance().currentUser?.email ?: "Farmer"
        binding.tvFarmerName.text = "Welcome, ${email.substringBefore('@').capitalize()}"
        binding.tvDate.text = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date())

        binding.btnScanTag.setOnClickListener {
            startActivity(Intent(requireContext(), TagScannerActivity::class.java))
        }
        binding.btnQuickAddCattle.setOnClickListener {
            startActivity(Intent(requireContext(), AddCattleActivity::class.java))
        }
        binding.btnQuickMilk.setOnClickListener {
            startActivity(Intent(requireContext(), MilkDiaryActivity::class.java))
        }
        binding.btnQuickVaccine.setOnClickListener {
            startActivity(Intent(requireContext(), VaccinationActivity::class.java))
        }
        binding.btnGetTip.setOnClickListener { getAiTip() }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val count = db.cattleDao().getCount()
            binding.tvCattleCount.text = count.toString()

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayMilk = db.milkDao().getTotalYieldForDate(today) ?: 0f
            binding.tvTodayMilk.text = "%.1f L".format(todayMilk)

            val pending = db.vaccinationDao().getPendingCount()
            binding.tvPendingVaccinations.text = "$pending vaccination(s) pending"
        }
    }

    private fun getAiTip() {
        binding.tvAiTip.text = "Getting tip..."
        binding.btnGetTip.isEnabled = false
        val messages = listOf(
            GroqMessage("user", "Give me one short practical cattle health tip for today. Max 2 sentences.")
        )
        GroqService.sendMessage(
            messages,
            onSuccess = { tip ->
                activity?.runOnUiThread {
                    binding.tvAiTip.text = tip
                    binding.btnGetTip.isEnabled = true
                }
            },
            onError = { err ->
                activity?.runOnUiThread {
                    binding.tvAiTip.text = "Could not get tip: $err"
                    binding.btnGetTip.isEnabled = true
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}