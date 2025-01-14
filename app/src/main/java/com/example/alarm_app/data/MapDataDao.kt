package com.example.alarm_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MapDataDao {
    @Insert
    suspend fun insertMapData(mapData: MapData)

    @Query("SELECT * FROM map_data")
    suspend fun getAllMapData(): List<MapData>
}
