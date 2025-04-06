package com.example.cloudnine.model.dataSource.repository

import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.WeatherResponse
import com.example.cloudnine.model.dataSource.local.WeatherLocalDataSource
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import com.example.cloudnine.model.dataSource.remote.WeatherRemoteDataSource
import com.example.cloudnine.model.dataSource.remote.model.City
import com.example.cloudnine.model.dataSource.remote.model.Clouds
import com.example.cloudnine.model.dataSource.remote.model.Coord
import com.example.cloudnine.model.dataSource.remote.model.List1
import com.example.cloudnine.model.dataSource.remote.model.Main
import com.example.cloudnine.model.dataSource.remote.model.Rain
import com.example.cloudnine.model.dataSource.remote.model.Sys
import com.example.cloudnine.model.dataSource.remote.model.Weather
import com.example.cloudnine.model.dataSource.remote.model.Wind
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {
    private val remote = mockk<WeatherRemoteDataSource>()
    private val local = mockk<WeatherLocalDataSource>()
    private lateinit var repository: WeatherRepository

    val sampleWeatherResponse = WeatherResponse(
        coord = Coord(lon = 31.2, lat = 30.1),
        weather = arrayListOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        base = "stations",
        main = Main(
            temp = 29.5,
            feelsLike = 30.0,
            tempMin = 28.0,
            tempMax = 32.0,
            pressure = 1012,
            seaLevel = 1012,
            grndLevel = 1005,
            humidity = 40
        ),
        visibility = 10000,
        wind = Wind(
            speed = 5.4,
            deg = 130,
            gust = 7.5
        ),
        rain = Rain(
            `1h` = 0.0,
            `3h` = 0.0
        ),
        clouds = Clouds(all = 0),
        dt = 1618317040L,
        sys = Sys(
            type = 1,
            id = 1234,
            country = "EG",
            sunrise = 1618282134,
            sunset = 1618327934
        ),
        timezone = 7200,
        id = 360630,
        name = "Cairo",
        cod = 200
    )

    val sampleForecastResponse = ForecastResponse(
        cod = "200",
        message = 0,
        cnt = 1,
        list = arrayListOf(
            List1(
                dt = 1618317040L,
                main = Main(
                    temp = 25.5,
                    feelsLike = 26.0,
                    tempMin = 24.0,
                    tempMax = 27.0,
                    pressure = 1009,
                    seaLevel = 1011,
                    grndLevel = 1002,
                    humidity = 50
                ),
                weather = arrayListOf(
                    Weather(
                        id = 500,
                        main = "Rain",
                        description = "light rain",
                        icon = "10d"
                    )
                ),
                clouds = Clouds(all = 75),
                wind = Wind(speed = 4.1, deg = 140, gust = 6.0),
                visibility = 10000,
                pop = 0.2,
                rain = Rain(`3h` = 0.15),
                sys = Sys(pod = "d"),
                dtTxt = "2025-04-06 12:00:00"
            )
        ),
        city = City(
            id = 360630,
            name = "Cairo",
            coord = Coord(lat = 30.1, lon = 31.2),
            country = "EG",
            population = 9500000,
            timezone = 7200,
            sunrise = 1618282134,
            sunset = 1618327934
        )
    )

    val city = FavoriteCity("Cairo", 30.0, 30.0)
    val favCityList = listOf<FavoriteCity>(city)
    val alarm = AlarmModel(1, 1234567890, "Cairo", 30.0, 30.0)
    val alarmsList = listOf<AlarmModel>(alarm)


    @Before
    fun setup() {
        repository = WeatherRepository(remote, local)
        coEvery {
            remote.getCurrentDayWeather(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        } returns flowOf(sampleWeatherResponse)

        coEvery {
            remote.getDailyForecasts(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        } returns flowOf(sampleForecastResponse)

        coEvery {
            local.getAllFavoriteCities()
        } returns flowOf(favCityList)

        coEvery {
            local.addFavoriteCity(city)
        } returns 100L

        coEvery { local.getAllAlarms() } returns flowOf(alarmsList)
        coEvery { local.getAlarmById(1) } returns alarm
    }

    @Test
    fun getRemoteCurrentDayWeather_returns_flow_from_remote() = runTest {
        val result = repository.getRemoteCurrentDayWeather(
            30.0,
            30.0,
            TemperatureUnit.CELSIUS,
            Language.ENGLISH
        )

        assertThat(result.first(), `is`(sampleWeatherResponse))

        coVerify {
            remote.getCurrentDayWeather(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        }
    }

    @Test
    fun getRemoteDailyForecasts_returns_flow_from_remote() = runTest {
        val result = repository.getRemoteDailyForecasts(
            30.0,
            30.0,
            TemperatureUnit.CELSIUS,
            Language.ENGLISH
        )

        assertThat(result.first(), `is`(sampleForecastResponse))

        coVerify {
            remote.getDailyForecasts(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        }
    }

    @Test
    fun getLocalAllFavoriteCities_returns_flow_from_local() = runTest {
        val result = repository.getLocalAllFavoriteCities()

        assertThat(result.first(), `is`(favCityList))

        coVerify {
            local.getAllFavoriteCities()
        }
    }

    @Test
    fun addLocalFavoriteCity_calls_local_and_returns_ID() = runTest {

        val result = repository.addLocalFavoriteCity(city)

        assertThat(result, `is` (100L))
        coVerify {
            local.addFavoriteCity(city)
        }
    }

    @Test
    fun getLocalAllAlarms_returns_flow_from_local() = runTest {

        val result = repository.getLocalAllAlarms()

        assertThat(result.first(), `is` (alarmsList))
        coVerify {
            local.getAllAlarms()
        }
    }

    @Test
    fun getLocalAlarmById_returns_specific_alarm() = runTest {
        val result = repository.getLocalAlarmById(1)

        assertThat(result, `is` (alarm))
        coVerify {
            local.getAlarmById(1)
        }
    }
}