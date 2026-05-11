package com.gokula.health.ui.milk

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gokula.health.databinding.FragmentCattleListBinding

class MilkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Navigate to milk diary directly
        startActivity(Intent(requireContext(), MilkDiaryActivity::class.java))
        return View(requireContext())
    }
}