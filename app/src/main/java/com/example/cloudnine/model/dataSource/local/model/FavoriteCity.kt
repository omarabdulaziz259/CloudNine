package com.example.cloudnine.model.dataSource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCity(
    @PrimaryKey
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)