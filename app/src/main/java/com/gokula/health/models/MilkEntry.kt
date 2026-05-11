package com.gokula.health.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cattleEarTagId: String = "",
    val date: String = "",
    val morningYield: Float = 0f,
    val eveningYield: Float = 0f,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val totalYield: Float get() = morningYield + eveningYield
}