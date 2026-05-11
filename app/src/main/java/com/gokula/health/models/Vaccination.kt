package com.gokula.health.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccinations")
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cattleEarTagId: String = "",
    val vaccineName: String = "",
    val scheduledDate: Long = 0L,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)