package com.gokula.health.database

import androidx.room.*
import com.gokula.health.models.MilkEntry

@Dao
interface MilkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MilkEntry): Long

    @Query("SELECT * FROM milk_entries WHERE cattleEarTagId = :id ORDER BY date DESC")
    suspend fun getByCattle(id: String): List<MilkEntry>

    @Query("SELECT * FROM milk_entries WHERE cattleEarTagId = :id ORDER BY date ASC LIMIT 30")
    suspend fun getLast30Days(id: String): List<MilkEntry>

    @Query("""
        SELECT AVG(morningYield + eveningYield) 
        FROM milk_entries 
        WHERE cattleEarTagId = :id AND date >= :startDate
    """)
    suspend fun getMonthlyAverage(id: String, startDate: String): Float?

    @Query("""
        SELECT SUM(morningYield + eveningYield) 
        FROM milk_entries 
        WHERE date = :date
    """)
    suspend fun getTotalYieldForDate(date: String): Float?

    @Delete
    suspend fun delete(entry: MilkEntry)
}