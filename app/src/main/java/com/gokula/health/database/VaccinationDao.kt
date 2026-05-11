package com.gokula.health.database

import androidx.room.*
import com.gokula.health.models.Vaccination

@Dao
interface VaccinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(v: Vaccination): Long

    @Query("SELECT * FROM vaccinations WHERE cattleEarTagId = :id ORDER BY scheduledDate ASC")
    suspend fun getByCattle(id: String): List<Vaccination>

    @Query("SELECT * FROM vaccinations WHERE isCompleted = 0 ORDER BY scheduledDate ASC")
    suspend fun getPending(): List<Vaccination>

    @Query("SELECT COUNT(*) FROM vaccinations WHERE isCompleted = 0")
    suspend fun getPendingCount(): Int

    @Update
    suspend fun update(v: Vaccination)

    @Delete
    suspend fun delete(v: Vaccination)
}