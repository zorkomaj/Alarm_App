package com.example.alarm_app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarm_data",
    foreignKeys = [
        ForeignKey(
            entity = MapData::class,
            parentColumns = ["id"],
            childColumns = ["mapDataId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AlarmData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alarmName: String,
    val mapDataId: Int,
    val checked: Boolean = false
)
