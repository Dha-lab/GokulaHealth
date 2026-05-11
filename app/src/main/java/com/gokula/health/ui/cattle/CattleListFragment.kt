package com.gokula.health.ui.cattle

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokula.health.adapters.CattleAdapter
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.FragmentCattleListBinding
import kotlinx.coroutines.launch

class CattleListFragment : Fragment() {
    private var _binding: FragmentCattleListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CattleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCattleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CattleAdapter { cattle ->
            startActivity(
                Intent(requireContext(), CattleDetailActivity::class.java)
                    .putExtra("earTag", cattle.earTagId)
            )
        }
        binding.recyclerCattle.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCattle.adapter = adapter

        binding.fabAddCattle.setOnClickListener {
            startActivity(Intent(requireContext(), AddCattleActivity::class.java))
        }
        loadCattle()
    }

    override fun onResume() {
        super.onResume()
        loadCattle()
    }

    private fun loadCattle() {
        lifecycleScope.launch {
            val list = AppDatabase.getInstance(requireContext()).cattleDao().getAll()
            adapter.submit(list)
            binding.tvNoCattle.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}