package com.example.cloudnine.home

import android.content.SharedPreferences
import com.example.cloudnine.home.viemModel.HomeViewModel
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.WeatherResponse
import com.example.cloudnine.model.dataSource.remote.model.City
import com.example.cloudnine.model.dataSource.remote.model.Clouds
import com.example.cloudnine.model.dataSource.remote.model.Coord
import com.example.cloudnine.model.dataSource.remote.model.List1
import com.example.cloudnine.model.dataSource.remote.model.Main
import com.example.cloudnine.model.dataSource.remote.model.Rain
import com.example.cloudnine.model.dataSource.remote.model.Sys
import com.example.cloudnine.model.dataSource.remote.model.Weather
import com.example.cloudnine.model.dataSource.remote.model.Wind
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.settings.SettingsHelper
import com.example.cloudnine.utils.LocationHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test


class HomeViewModelTest {
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

    private lateinit var weatherRepoStub: WeatherRepository
    private lateinit var locationHelper: LocationHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var homeViewModel: HomeViewModel


    @Before
    fun setup() {
        weatherRepoStub = mockk()
        locationHelper = mockk()
        sharedPreferences = mockk()

        coEvery {
            sharedPreferences.getString(SettingsHelper.API_LANGUAGE_PREF, "English")
        } returns "English"

        homeViewModel = HomeViewModel(weatherRepoStub, locationHelper, sharedPreferences)

        coEvery {
            weatherRepoStub.getRemoteCurrentDayWeather(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        } returns flowOf(sampleWeatherResponse)

        coEvery {
            weatherRepoStub.getRemoteDailyForecasts(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        } returns flowOf(sampleForecastResponse)
    }


    @Test
    fun getCurrentDayWeather_should_emit_Success() = runTest {
        homeViewModel.getCurrentDayWeather(30.0, 30.0, Language.ENGLISH, TemperatureUnit.CELSIUS)


        val result = homeViewModel.weatherResponse.value
        if (result is Response.Success) {
            assertNotNull(result.data)
            assertThat(result.data, `is`(sampleWeatherResponse))
        }
        coVerify {
            weatherRepoStub.getRemoteCurrentDayWeather(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        }
    }

    @Test
    fun getDailyForecasts_should_emit_Success() = runTest {
        homeViewModel.getDailyForecasts(30.0, 30.0, Language.ENGLISH, TemperatureUnit.CELSIUS)


        val result = homeViewModel.forecastResponse.value
        if (result is Response.Success) {
            assertNotNull(result.data)
            assertThat(result.data, `is`(sampleForecastResponse))
        }
        coVerify {
            weatherRepoStub.getRemoteDailyForecasts(
                30.0,
                30.0,
                TemperatureUnit.CELSIUS,
                Language.ENGLISH
            )
        }
    }
}