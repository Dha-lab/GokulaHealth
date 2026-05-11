package com.gokula.health.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gokula.health.R
import com.gokula.health.databinding.ActivityMainBinding
import com.gokula.health.ui.ai.GroqAssistantActivity
import com.gokula.health.ui.alerts.AlertsFragment
import com.gokula.health.ui.cattle.CattleListFragment
import com.gokula.health.ui.home.HomeFragment
import com.gokula.health.ui.milk.MilkFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_cattle -> loadFragment(CattleListFragment())
                R.id.nav_milk -> loadFragment(MilkFragment())
                R.id.nav_alerts -> loadFragment(AlertsFragment())
                R.id.nav_ai -> {
                    startActivity(Intent(this, GroqAssistantActivity::class.java))
                    false
                }
                else -> false
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}