package com.example.alarm_app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MapData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mapDataDao(): MapDataDao
}
