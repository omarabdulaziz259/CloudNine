package com.example.cloudnine.model.dataSource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.cloudnine.model.dataSource.local.alarm.AlarmDao
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.FavoriteCityDao
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest {
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var db: WeatherDatabase
    private lateinit var favCityDao: FavoriteCityDao
    private lateinit var alarmDao: AlarmDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries().build()
        favCityDao = db.getFavoriteCityDao()
        alarmDao = db.getAlarmDao()
        localDataSource = WeatherLocalDataSource(favCityDao, alarmDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertCity_and_getAllCities_shouldContainInsertedCity() = runTest {
        val favCity = FavoriteCity("Cairo", 30.0, 30.0)
        localDataSource.addFavoriteCity(favCity)

        val cities = localDataSource.getAllFavoriteCities().first()
        assertThat(cities.size, not(0))
        assertThat(cities[0], `is`(favCity))
    }

    @Test
    fun insertDuplicateCity_shouldNotInsertAgain() = runTest {
        val favCity = FavoriteCity("Cairo", 30.0, 30.0)
        val firstInsert = localDataSource.addFavoriteCity(favCity)
        val secondInsert = localDataSource.addFavoriteCity(favCity)

        val cities = localDataSource.getAllFavoriteCities().first()
        assertThat(cities.size, `is`(1))
        assertThat(firstInsert, not(-1))
        assertThat(secondInsert, `is`(-1))
    }

    @Test
    fun deleteCity_shouldRemoveFromDatabase() = runTest {
        val favCity = FavoriteCity("Cairo", 30.0, 30.0)
        localDataSource.addFavoriteCity(favCity)
        localDataSource.deleteFavoriteCity(favCity)

        val cities = localDataSource.getAllFavoriteCities().first()
        assertThat(cities.size, `is`(0))
    }

    @Test
    fun insertAlarm_and_getAllAlarms_shouldContainsInsertedAlarm() = runTest {
        val alarmInstance = AlarmModel(
            id = 1,
            city = "Cairo",
            lon = 30.0,
            lat = 30.0,
            triggerTimeInMillis = 12345678901234,
        )
        localDataSource.insertAlarm(alarmInstance)

        val alarms = localDataSource.getAllAlarms().first()
        assertThat(alarms.size, not(0))
        assertThat(alarms[0], `is`(alarmInstance))
    }

    @Test
    fun getAlarmById_shouldReturnCorrectAlarm() = runTest {
        val alarmInstance = AlarmModel(
            id = 1,
            city = "Cairo",
            lon = 30.0,
            lat = 30.0,
            triggerTimeInMillis = 12345678901234,
        )
        localDataSource.insertAlarm(alarmInstance)

        val result = localDataSource.getAlarmById(alarmInstance.id)
        assertThat(result, `is`(alarmInstance))
    }

    @Test
    fun deleteAlarm_shouldRemoveAlarmFromDatabase() = runTest {
        val alarmInstance = AlarmModel(
            id = 1,
            city = "Cairo",
            lon = 30.0,
            lat = 30.0,
            triggerTimeInMillis = 12345678901234,
        )
        localDataSource.insertAlarm(alarmInstance)

        localDataSource.deleteAlarm(alarmInstance)

        val alarms = localDataSource.getAllAlarms().first()
        assertThat(alarms.size, `is`(0))
    }

}