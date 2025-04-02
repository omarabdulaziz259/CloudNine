package com.example.cloudnine.home

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.cloudnine.model.dataSource.Response
import com.example.cloudnine.utils.LocationHelper
import com.example.cloudnine.utils.convertUnixTimestampToDateTime

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    homeViewModel.fetchWeatherBasedOnPreference()
    val weatherState = homeViewModel.weatherResponse.collectAsState().value
    LaunchedEffect(weatherState) {
        when (weatherState) {
            is Response.Success -> {
                // Perform any action when new weather data is available
                println("Weather data updated: ${weatherState.data}")
            }
            is Response.Failure -> {
                println("Weather data failed to load: ${weatherState.error.message}")
            }
            else -> {}
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        when (weatherState) {
            is Response.Loading -> {
                Text(text = stringResource(R.string.loading))
            }

            is Response.Success -> {
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
                val dateAndTime = convertUnixTimestampToDateTime(weatherData.dt ?: 0)
                val weatherIcon = weatherData.weather.firstOrNull()?.icon ?: ""

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    GlideImage(
                        model = "https://openweathermap.org/img/wn/" + "${weatherIcon}@3x.png",
                        contentDescription = "Weather Descriptive Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "$currentTemperature °$tempUnit")
                }

                Text(text = cityName ?: stringResource(R.string.n_a))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 2.dp, color = Color.Blue)
                ) {
                    Column {
                        Text(text = "Humidity: $humidity%")
                        Text(text = "Clouds: $cloudCoverage%")
                        Text(text = "Wind Speed: $windSpeed $speedUnit")
                        Text(text = "Pressure: $pressure hPa")
                    }
                }
            }

            is Response.Failure -> {
                Text(text = "Error: ${weatherState.error.message}")
            }
        }
    }
}
//
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text(text = , modifier = Modifier.align(Alignment.CenterVertically))
//            Column(horizontalAlignment = Alignment.End) {
//                Text(text = "Date")
//                Text(text = "Time")
//            }
//        }
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//            // Placeholder for weather icon
//            Text(text = "Weather Icon", modifier = Modifier.align(Alignment.CenterVertically))
//        }
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//            Text(text = "Current Temperature °C")
//        }
//        Text(text = "City Name")
//        Box(modifier = Modifier.fillMaxWidth()) {
//            Column {
//                Text(text = "Humidity: XX%")
//                Text(text = "Cloud Coverage: XX%")
//                Text(text = "Wind Speed: XX km/h")
//                Text(text = "Pressure: XX hPa")
//            }
//        }
//        LazyRow {
//            items(5) { index ->
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(text = "Time $index")
//                    // Placeholder for hourly weather icon
//                    Text(text = "Hourly Icon")
//                    Text(text = "Temperature °C")
//                }
//            }
//        }
//        LazyRow {
//            items(5) { index ->
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(text = "Date $index")
//                    Text(text = "High: XX°C")
//                    Text(text = "Low: XX°C")
//                }
//            }
//        }