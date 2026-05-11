package com.gokula.health.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cattle")
data class Cattle(
    @PrimaryKey
    val earTagId: String = "",
    val name: String = "",
    val breed: String = "",
    val age: Int = 0,
    val weight: Float = 0f,
    val photoPath: String = "",
    val photoUrl: String = "",
    val ownerId: String = "",
    val healthStatus: String = "Healthy",
    val createdAt: Long = System.currentTimeMillis()
)