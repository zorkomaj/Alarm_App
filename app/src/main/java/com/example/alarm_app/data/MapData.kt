package com.example.alarm_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_data")
data class MapData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-increment primary key
    val alarmName: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float
)
