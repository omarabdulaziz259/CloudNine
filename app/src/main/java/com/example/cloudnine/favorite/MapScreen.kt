package com.example.cloudnine.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.cloudnine.R
import com.example.cloudnine.model.Response
import com.example.cloudnine.navigation.BottomNavItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    fromSetting: Boolean = false,
    navController: NavController,
    favViewModel: FavoriteViewModel
) {
    var defaultLocation by remember { mutableStateOf(LatLng(30.0444,  31.2357))}
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(defaultLocation, 10f) }
    var markerTitle by remember {mutableStateOf("Egypt")}
    val context = LocalContext.current

    LaunchedEffect (defaultLocation) {
        cameraPositionState.animate(update = CameraUpdateFactory.newLatLngZoom(defaultLocation,10f))
        markerTitle = favViewModel.getCountryName(context, defaultLocation.latitude, defaultLocation.longitude) ?: ""
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true,
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
            ),
            onMapClick = { coordinates ->
                defaultLocation = coordinates

            }
        ) {
            Marker(
                state = MarkerState(position = defaultLocation),
                title = markerTitle,
            )
        }
    }
    if(fromSetting) {
        FromSettingConfiguration(favViewModel,navController,defaultLocation.latitude, defaultLocation.longitude)
    }else{
        ShowCardDetails(defaultLocation.latitude, defaultLocation.longitude, favViewModel)
    }
}

@Composable
fun ShowCardDetails(lat: Double, lon: Double, favViewModel: FavoriteViewModel) {

    val forecastResponse = favViewModel.forecastResponse.collectAsStateWithLifecycle().value
    favViewModel.getDailyForecasts(lat = lat, lon = lon, temperatureUnit = favViewModel.units, language = favViewModel.language)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        when(forecastResponse) {
            is Response.Loading -> {
                CircularProgressIndicator()
            }
            is Response.Failure -> {
                Text(
                    text = "${forecastResponse.error.message}",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is Response.Success -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 20.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(Color(0xFF424242)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.latitude),
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = forecastResponse.data.city?.coord?.lat.toString(),
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.longitude),
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = forecastResponse.data.city?.coord?.lon.toString(),
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.country_name),
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = favViewModel.getCountryName(forecastResponse.data.city?.country),
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Button(
                            onClick = {
                                forecastResponse.data.city?.let {
                                    favViewModel.insertFavCity(it.name ?: "", it.coord?.lon ?: 0.0, it.coord?.lat ?: 0.0)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E88E5),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.add_to_favourite),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FromSettingConfiguration(favViewModel: FavoriteViewModel,navController: NavController,lat : Double, lon : Double ){
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){
        Button(
            onClick = {
                favViewModel.setAppLatitude(lat)
                favViewModel.setAppLongitude(lon)
                navController.navigate(BottomNavItem.Settings.route)
            },
            colors = ButtonDefaults.buttonColors(Color.Black)

        ) {
            Text(
                text = stringResource(R.string.set_your_location),
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}