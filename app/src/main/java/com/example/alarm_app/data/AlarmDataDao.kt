package com.example.alarm_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlarmDataDao {
    @Insert
    suspend fun insertAlarmData(alarmData: AlarmData)

    @Query("""
        SELECT alarm_data.id, alarm_data.alarmName, map_data.locationName, alarm_data.checked 
        FROM alarm_data 
        INNER JOIN map_data ON alarm_data.mapDataId = map_data.id
    """)
    suspend fun getAllAlarmData(): List<AlarmWithLocation>

    @Query("""
    SELECT map_data.* FROM alarm_data
    INNER JOIN map_data ON alarm_data.mapDataId = map_data.id
    WHERE alarm_data.checked = 1
""")
    suspend fun getCheckedGeofences(): List<MapData>
}

data class AlarmWithLocation(
    val id: Int,
    val alarmName: String,
    val locationName: String,
    val checked: Boolean
)
