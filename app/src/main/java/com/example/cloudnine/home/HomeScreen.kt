package com.example.cloudnine.home

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.cloudnine.R
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.Response
import com.example.cloudnine.utils.LocationHelper
import com.example.cloudnine.utils.convertUnixTimestampToDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    homeViewModel.fetchWeatherBasedOnPreference()
    val weatherState = homeViewModel.weatherResponse.collectAsState().value
    val forecastState = homeViewModel.forecastResponse.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        when {
            forecastState is Response.Loading && weatherState is Response.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            forecastState is Response.Success && weatherState is Response.Success -> {

                var tempUnit = when (homeViewModel.unit) {
                    TemperatureUnit.KELVIN -> stringResource(R.string.k)
                    TemperatureUnit.CELSIUS -> stringResource(R.string.c)
                    TemperatureUnit.FAHRENHEIT -> stringResource(R.string.f)
                }
                var speedUnit = when (tempUnit) {
                    stringResource(R.string.f) -> stringResource(R.string.km_h)
                    else -> stringResource(R.string.mph)
                }

                val weatherData = weatherState.data
                val forecastData = forecastState.data

                val currentTemperature = weatherData.main?.temp
                val cityName = weatherData.name
                val humidity = weatherData.main?.humidity
                val windSpeed = weatherData.wind?.speed
                val pressure = weatherData.main?.pressure
                val cloudCoverage = weatherData.clouds?.all
                val weatherDescription =
                    weatherData.weather.firstOrNull()?.description ?: stringResource(
                        R.string.n_a
                    )
                val dateAndTime = convertUnixTimestampToDateTime(weatherData.dt ?: 0)
                val weatherIcon = weatherData.weather.firstOrNull()?.icon ?: ""
//                val ayhaga = forecastData.list.get(0).

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weatherDescription,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = dateAndTime,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlideImage(
                            model = "https://openweathermap.org/img/wn/${weatherIcon}@2x.png",
                            contentDescription = "Weather Descriptive Icon",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "$currentTemperature °$tempUnit")
                        Text(text = cityName ?: stringResource(R.string.n_a))
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .border(width = 2.dp, color = Color.Black),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Text(text = "Humidity: $humidity%")
                        Text(text = "Clouds: $cloudCoverage%")
                    }
                    Column(Modifier.padding(10.dp)) {
                        Text(text = "Wind Speed: $windSpeed $speedUnit")
                        Text(text = "Pressure: $pressure hPa")
                    }
                }

                val groupedForecast = forecastData.list.groupBy { it.dtTxt?.substring(0, 10) }
                val todayKey = groupedForecast.keys.firstOrNull()
                val todayForecast = groupedForecast[todayKey] ?: emptyList()
                val upcomingDays = groupedForecast.filterKeys { it != todayKey }

                LazyRow(
                    Modifier
                        .padding(10.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                ) {
                    items(todayForecast.size) { index ->
                        Column(
                            modifier = Modifier
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = convertUnixTimestampToDateTime(
                                    todayForecast[index].dt ?: 0
                                ).substring(0, 3)
                            )
                            GlideImage(
                                model = "https://openweathermap.org/img/wn/${todayForecast[index].weather.firstOrNull()?.icon}@2x.png",
                                contentDescription = "Today's Weather Icon",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "${todayForecast[index].main?.temp}°$tempUnit",
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                LazyRow(
                    Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                ) {
                    items(upcomingDays.entries.toList()) { (_, forecasts) ->
                        val minTemp =
                            forecasts.minOfOrNull { it.main?.temp ?: Double.MIN_VALUE } ?: 0.0
                        val maxTemp =
                            forecasts.maxOfOrNull { it.main?.temp ?: Double.MAX_VALUE } ?: 0.0

                        Column(
                            modifier = Modifier
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                            Log.i("TAG", "HomeScreen: $date")

                            Text(text = convertUnixTimestampToDateTime(
                                forecasts[0].dt ?: 0
                            ).substring(0, 3))
                            Text(text = "H: ${maxTemp.roundToInt()}°$tempUnit")
                            Text(text = "L: ${minTemp.roundToInt()}°$tempUnit")
                        }
                    }
                }
            }
//                LazyRow (
//                    Modifier.padding(10.dp)
//                        .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
//                ){
//                    items(upcomingDays.entries.toList()) { (date, list) ->
//                        Column(
//                            modifier = Modifier
//                                .padding(10.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(text = convertUnixTimestampToDateTime(upcomingDays.entries.toList()[index].value[index].dt ?: 0).substring(0, 3))
//                            GlideImage(
//                                model = "https://openweathermap.org/img/wn/${todayForecast[index].weather.firstOrNull()?.icon}@2x.png",
//                                contentDescription = "Today's Weather Icon",
//                                modifier = Modifier.size(40.dp)
//                            )
//                            Text(text = "${todayForecast[index].main?.temp}°$tempUnit", modifier = Modifier.padding(5.dp))
//                        }
//                    }
//                }
//            }

                forecastState is Response.Failure && weatherState is Response.Failure -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${weatherState.error.message}")
                    }
                }
            }
        }
    }
//
//LazyRow {
//    items(5) { index ->
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "Time $index")
//            Spacer(Modifier.height(6.dp))
//            Text(text = "Hourly Icon")
//            Spacer(Modifier.height(6.dp))
//            Text(text = "Temperature °C")
//        }
//    }
//}
//
//LazyRow {
//    items(5) { index ->
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "Date $index")
//            Text(text = "High: XX°C")
//            Text(text = "Low: XX°C")
//        }
//    }
//}