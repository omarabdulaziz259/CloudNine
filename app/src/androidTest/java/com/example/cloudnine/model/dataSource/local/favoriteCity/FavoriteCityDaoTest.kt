package com.example.cloudnine.model.dataSource.local.favoriteCity

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.cloudnine.model.dataSource.local.WeatherDatabase
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class FavoriteCityDaoTest {
    private lateinit var db: WeatherDatabase
    private lateinit var dao: FavoriteCityDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .build()
        dao = db.getFavoriteCityDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertFavCity_and_getAllCities_returnsCity() = runTest {
        val favCity = FavoriteCity("Cairo", 30.0, 30.0)
        dao.insertFavCity(favCity)

        val result = dao.getAllCities().first().first()
        assertThat(result.cityName, `is` (favCity.cityName))
        assertThat(result.latitude, `is` (favCity.latitude))
        assertThat(result.longitude, `is` (favCity.longitude))
    }

    @Test
    fun deleteFavCity_removesCity() = runTest {
        val favCity1 = FavoriteCity("cairo", 30.0, 30.0)
        val favCity2 = FavoriteCity("Alex", 20.0, 20.0)
        dao.insertFavCity(favCity1)
        dao.insertFavCity(favCity2)
        dao.deleteFavCity(favCity1)

        val result = dao.getAllCities().first().first()
        assertThat(result.cityName, not(favCity1.cityName))
        assertThat(result.longitude, not(favCity1.longitude))
        assertThat(result.latitude, not(favCity1.latitude))
        assertThat(result.cityName, `is`(favCity2.cityName))
        assertThat(result.longitude, `is`(favCity2.longitude))
        assertThat(result.latitude, `is`(favCity2.latitude))
    }

    @Test
    fun insertDuplicateCity_shouldIgnore() = runTest {
        val favCity = FavoriteCity("Cairo", 30.0, 30.0)
        val id1 = dao.insertFavCity(favCity)
        val id2 = dao.insertFavCity(favCity)

        val result = dao.getAllCities().first()
        assertThat(result.size, `is` (1))
        assertThat(id1, not (-1))
        assertThat(id2, `is`(-1))
    }
}