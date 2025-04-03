package com.example.cloudnine.model.dataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.cloudnine.model.dataSource.local.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavCity(favoriteCity: FavoriteCity) : Long

    @Delete
    suspend fun deleteFavCity(favoriteCity: FavoriteCity) : Int

    @Query("SELECT * FROM favorite_cities")
    fun getAllCities(): Flow<List<FavoriteCity>>
}
