package com.example.cloudnine.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.cloudnine.R
import com.example.cloudnine.home.viemModel.HomeViewModel
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.Response
import com.example.cloudnine.utils.convertTimestampToDateTime
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
                val dateAndTime =
                    convertTimestampToDateTime(weatherData.dt ?: 0, homeViewModel.langPref)
                val weatherIcon = weatherData.weather.firstOrNull()?.icon ?: ""

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(140.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.5f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = weatherDescription,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(140.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.5f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dateAndTime,
                            fontSize = 16.sp,
                            color = Color.White,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(160.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.5f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlideImage(
                            model = "https://openweathermap.org/img/wn/${weatherIcon}@2x.png",
                            contentDescription = "Weather Descriptive Icon",
                            modifier = Modifier.size(60.dp)
                        )
                        Text(
                            text = "$currentTemperature °$tempUnit", fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = cityName ?: stringResource(R.string.n_a), fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        Modifier
                            .padding(10.dp)
                            .width(160.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.5f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "${stringResource(R.string.humidity)}: $humidity%",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${stringResource(R.string.clouds)}: $cloudCoverage",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                    Column(
                        Modifier
                            .padding(10.dp)
                            .width(160.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.5f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(
                            text = "${stringResource(R.string.wind_speed)}: $windSpeed $speedUnit",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${stringResource(R.string.pressure)}: $pressure ${
                                stringResource(
                                    R.string.hpa
                                )
                            }",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                val forecastData = forecastState.data
                val groupedForecast = forecastData.list.groupBy { it.dtTxt?.substring(0, 10) }
                val todayKey = groupedForecast.keys.firstOrNull()
                val todayForecast = groupedForecast[todayKey] ?: emptyList()
                val upcomingDays = groupedForecast.filterKeys { it != todayKey }

                LazyRow(
                    Modifier
                        .padding(10.dp)
                ) {
                    items(todayForecast.size) { index ->
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .width(100.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.5f)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = convertTimestampToDateTime(
                                    todayForecast[index].dt ?: 0, homeViewModel.langPref
                                ).split(',')[0],
                                fontSize = 16.sp,
                                color = Color.White

                            )
                            GlideImage(
                                model = "https://openweathermap.org/img/wn/${todayForecast[index].weather.firstOrNull()?.icon}@2x.png",
                                contentDescription = "Today's Weather Icon",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "${todayForecast[index].main?.temp}°$tempUnit",
                                fontSize = 16.sp,
                                color = Color.White,
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
                ) {
                    items(upcomingDays.entries.toList()) { (_, forecasts) ->
                        val minTemp =
                            forecasts.minOfOrNull { it.main?.temp ?: Double.MIN_VALUE } ?: 0.0
                        val maxTemp =
                            forecasts.maxOfOrNull { it.main?.temp ?: Double.MAX_VALUE } ?: 0.0

                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .width(100.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = convertTimestampToDateTime(
                                    forecasts[0].dt ?: 0, homeViewModel.langPref
                                ).split(',')[0],
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${stringResource(R.string.h)}: ${maxTemp.roundToInt()}°$tempUnit",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${stringResource(R.string.l)}: ${minTemp.roundToInt()}°$tempUnit",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            forecastState is Response.Failure && weatherState is Response.Failure -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.error_fetching_data_for_this_city))
                }
            }
        }
    }
}