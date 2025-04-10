package com.example.cloudnine.model.dataSource.local.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity

@Entity(tableName = "alarm_table")
data class AlarmModel(
    @PrimaryKey
    val id: Int,
    val triggerTimeInMillis: Long,
    val city: String,
    val lon: Double,
    val lat: Double
)