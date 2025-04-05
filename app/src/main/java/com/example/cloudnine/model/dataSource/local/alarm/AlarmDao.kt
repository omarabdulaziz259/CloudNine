package com.example.cloudnine.model.dataSource.local.alarm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmModel): Long

    @Delete
    suspend fun deleteAlarm(alarm: AlarmModel) : Int

    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)

    @Query("SELECT * FROM alarm_table WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Int): AlarmModel?

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<AlarmModel>>
}