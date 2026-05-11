package com.gokula.health.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gokula.health.models.Cattle
import com.gokula.health.models.MilkEntry
import com.gokula.health.models.Vaccination

@Database(
    entities = [Cattle::class, MilkEntry::class, Vaccination::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cattleDao(): CattleDao
    abstract fun milkDao(): MilkDao
    abstract fun vaccinationDao(): VaccinationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gokula_health_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}