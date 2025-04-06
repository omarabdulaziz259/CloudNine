package com.example.cloudnine.favorite.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.cloudnine.R
import com.example.cloudnine.favorite.viewModel.FavoriteViewModel
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import com.example.cloudnine.utils.convertTimestampToDateTime
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.roundToInt
import kotlin.text.split

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(favoriteViewModel: FavoriteViewModel, navController: NavController) {
    val favoriteCities = favoriteViewModel.favCitiesResponse.collectAsStateWithLifecycle().value
    favoriteViewModel.getAllFavCities()

    var cityToDelete = remember { mutableStateOf<FavoriteCity?>(null) }

    var selectedCity = remember { mutableStateOf<FavoriteCity?>(null) }
    var isBottomSheetVisible = remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map_screen_from_fav") }
            ) {
                Image(painter = painterResource(R.drawable.maps_location), contentDescription = "Add City", modifier = Modifier.size(30.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (favoriteCities) {
                is Response.Loading -> {
                    CircularProgressIndicator()
                }

                is Response.Success -> {
                    if (favoriteCities.data != null && favoriteCities.data.isNotEmpty()) {
                        LazyColumn {
                            items(favoriteCities.data.size) { index ->
                                CityItem(
                                    favoriteCities.data[index],
                                    onClick = { city ->
                                            isBottomSheetVisible.value = true
                                            selectedCity.value = city
                                    },
                                    onDeleteClick = { city -> cityToDelete.value = city }
                                )
                            }
                        }
                    } else {
                        Text(
                            stringResource(R.string.no_favorite_cities),
                            color = Color.White,
                            fontSize = 25.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is Response.Failure -> {
                    Text(
                        stringResource(R.string.error_fetching_favorite_cities),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    cityToDelete.value?.let { city ->
        DeleteConfirmationDialog(
            cityName = city.cityName,
            onConfirm = {
                favoriteViewModel.deleteFavCity(city)
                cityToDelete.value = null
            },
            onDismiss = { cityToDelete.value = null }
        )
    }

    if (isBottomSheetVisible.value && selectedCity.value != null) {
        ModalBottomSheet(
            onDismissRequest = {
                isBottomSheetVisible.value = false
                selectedCity.value = null
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = Color(0xFFFFFFFF)
        ) {
            selectedCity.value?.let {
                WeatherDetailsBottomSheet(
                    city = it,
                    favoriteViewModel = favoriteViewModel
                )
            }
        }
    }
}

@Composable
fun CityItem(
    city: FavoriteCity,
    onClick: (FavoriteCity) -> Unit,
    onDeleteClick: (FavoriteCity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(city) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color(0xFF424242))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(city.cityName, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "${stringResource(R.string.latitude)}: ${city.latitude}",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                "${stringResource(R.string.longitude)}: ${city.longitude}",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onDeleteClick(city) },
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete City",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(cityName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_city)) },
        text = { Text("${stringResource(R.string.are_you_sure_you_want_to_delete)} $cityName ${stringResource(R.string.question_mark)}") },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(R.string.yes)) }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(stringResource(R.string.no)) }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherDetailsBottomSheet(city: FavoriteCity, favoriteViewModel: FavoriteViewModel) {
    val weatherResponse = favoriteViewModel.weatherResponse.collectAsStateWithLifecycle().value
    val forecastResponse = favoriteViewModel.forecastResponse.collectAsStateWithLifecycle().value
    favoriteViewModel.updatePref()
    favoriteViewModel.getForecastForCity(city)
    favoriteViewModel.getWeatherForCity(city)
    when {
        weatherResponse is Response.Loading && forecastResponse is Response.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        weatherResponse is Response.Failure && forecastResponse is Response.Failure -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.error_fetching_data_for_this_city))
            }
        }

        weatherResponse is Response.Success && forecastResponse is Response.Success -> {


            var tempUnit = when (favoriteViewModel.units) {
                TemperatureUnit.KELVIN -> stringResource(R.string.k)
                TemperatureUnit.CELSIUS -> stringResource(R.string.c)
                TemperatureUnit.FAHRENHEIT -> stringResource(R.string.f)
            }
            var speedUnit = when (tempUnit) {
                stringResource(R.string.f) -> stringResource(R.string.km_h)
                else -> stringResource(R.string.mph)
            }

            val weatherData = weatherResponse.data

            val currentTemperature = weatherData.main?.temp
            val humidity = weatherData.main?.humidity
            val windSpeed = weatherData.wind?.speed
            val pressure = weatherData.main?.pressure
            val cloudCoverage = weatherData.clouds?.all
            val weatherDescription =
                weatherData.weather.firstOrNull()?.description ?: stringResource(
                    R.string.n_a
                )
            val dateAndTime = convertTimestampToDateTime(weatherData.dt ?: 0, favoriteViewModel.langPref)
            val weatherIcon = weatherData.weather.firstOrNull()?.icon ?: ""

            val forecastData = forecastResponse.data
            val groupedForecast = forecastData.list.groupBy { it.dtTxt?.substring(0, 10) }
            val todayKey = groupedForecast.keys.firstOrNull()
            val todayForecast = groupedForecast[todayKey] ?: emptyList()
            val upcomingDays = groupedForecast.filterKeys { it != todayKey }

            val cityName = city.cityName



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
                    Text(text = cityName)
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
                    Text(text = "${stringResource(R.string.humidity)}: $humidity%")
                    Text(text = "${stringResource(R.string.clouds)}: $cloudCoverage%")
                }
                Column(Modifier.padding(10.dp)) {
                    Text(text = "${stringResource(R.string.wind_speed)} : $windSpeed $speedUnit")
                    Text(text = "${stringResource(R.string.pressure)}: $pressure ${stringResource(R.string.hpa)}")
                }
            }

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
                            text = convertTimestampToDateTime(
                                todayForecast[index].dt ?: 0, favoriteViewModel.langPref
                            ).split(',')[0]
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

                        Text(
                            text = convertTimestampToDateTime(
                                forecasts[0].dt ?: 0, favoriteViewModel.langPref
                            ).split(',')[0]
                        )
                        Text(text = "${stringResource(R.string.h)}: ${maxTemp.roundToInt()}°$tempUnit")
                        Text(text = "${stringResource(R.string.l)}: ${minTemp.roundToInt()}°$tempUnit")
                    }
                }
            }
        }
    }
}
