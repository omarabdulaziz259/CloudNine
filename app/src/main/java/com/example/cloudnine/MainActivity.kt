package com.example.cloudnine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cloudnine.alert.AlertScreen
import com.example.cloudnine.favorite.FavoriteScreen
import com.example.cloudnine.home.HomeScreen
import com.example.cloudnine.home.HomeViewModel
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.navigation.BottomNavItem
import com.example.cloudnine.navigation.BottomNavigationBar
import com.example.cloudnine.settings.SettingsScreen
import com.example.cloudnine.utils.LocationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherRepository = WeatherRepository.getInstance(this)
        val locationHelper = LocationHelper(this)
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val homeViewModel = HomeViewModel(weatherRepository, locationHelper, sharedPreferences)
        locationHelper.getLocation()
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    BottomNavigationBar(navController, currentRoute ?: "")
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    NavigationGraph(navController, homeViewModel, locationHelper)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, homeViewModel: HomeViewModel, locationHelper: LocationHelper) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(homeViewModel) }
        composable(BottomNavItem.Favorite.route) { FavoriteScreen() }
        composable(BottomNavItem.Alert.route) { AlertScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen() }
    }
}
//        Log.i("TAG", "onCreate: ${BuildConfig.API_KEY}")
//        val weatherRemoteDataSource = WeatherRemoteDataSource.getInstance()
//        var weatherResponse by mutableStateOf<WeatherResponse?>(null)
//        lifecycleScope.launch {
//            try {
//                weatherRemoteDataSource.getCurrentDayWeather(
//                    lon = 10.99,
//                    lat = 44.34,
//                    units = TemperatureUnit.KELVIN,
//                    language = Language.ENGLISH
//                ).collect { response ->
//                    Log.i("MainActivity", "Weather Data: $response")
//                    weatherResponse = response
//                }
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Error fetching weather", e)
//            }
//        }
//
//        setContent {
//            weatherResponse?.let {
//                Text(text = it.name.toString())
//            } ?: Text(text = "Loading...")
//        }
