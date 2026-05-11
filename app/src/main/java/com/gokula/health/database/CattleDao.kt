package com.gokula.health.database

import androidx.room.*
import com.gokula.health.models.Cattle

@Dao
interface CattleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cattle: Cattle)

    @Query("SELECT * FROM cattle ORDER BY createdAt DESC")
    suspend fun getAll(): List<Cattle>

    @Query("SELECT * FROM cattle WHERE earTagId = :id LIMIT 1")
    suspend fun getById(id: String): Cattle?

    @Query("SELECT * FROM cattle WHERE ownerId = :uid ORDER BY createdAt DESC")
    suspend fun getByOwner(uid: String): List<Cattle>

    @Delete
    suspend fun delete(cattle: Cattle)

    @Query("SELECT COUNT(*) FROM cattle")
    suspend fun getCount(): Int
}