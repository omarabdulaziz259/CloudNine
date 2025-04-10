package com.example.cloudnine

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cloudnine.alert.view.AlertScreen
import com.example.cloudnine.alert.viewModel.AlertViewModel
import com.example.cloudnine.favorite.view.FavoriteScreen
import com.example.cloudnine.favorite.viewModel.FavoriteViewModel
import com.example.cloudnine.favorite.view.MapScreen
import com.example.cloudnine.home.view.HomeScreen
import com.example.cloudnine.home.viemModel.HomeViewModel
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.navigation.BottomNavItem
import com.example.cloudnine.navigation.BottomNavigationBar
import com.example.cloudnine.settings.SettingsHelper
import com.example.cloudnine.settings.view.SettingsScreen
import com.example.cloudnine.settings.viewModel.SettingsViewModel
import com.example.cloudnine.utils.LocationHelper
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    override fun attachBaseContext(newBase: Context?) {
        val localeUpdatedContext = newBase?.let { applyAppLocale(it) }
        super.attachBaseContext(localeUpdatedContext)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Notification permission approved", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        val weatherRepository = WeatherRepository.getInstance(this)
        val locationHelper = LocationHelper(this)
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val systemLanguage = Locale.getDefault().language
        val appLangPref = sharedPreferences.getString(SettingsHelper.APP_LANGUAGE_PREF, "Default")
        if (systemLanguage.equals("ar", ignoreCase = true) && appLangPref.equals(
                "Default",
                ignoreCase = true
            )
        ) {
            sharedPreferences.edit().putString(SettingsHelper.API_LANGUAGE_PREF, "Arabic").apply()
        } else if (systemLanguage.equals("en", ignoreCase = true) && appLangPref.equals(
                "Default",
                ignoreCase = true
            )
        ) {
            sharedPreferences.edit().putString(SettingsHelper.API_LANGUAGE_PREF, "English").apply()
        }
        Log.i("TAG", "onCreate: system lang is $systemLanguage")
        val homeViewModel = HomeViewModel(weatherRepository, locationHelper, sharedPreferences)
        val favoriteViewModel = FavoriteViewModel(weatherRepository, sharedPreferences)
        val alertViewModel = AlertViewModel(weatherRepository, sharedPreferences)
        locationHelper.getLocation()
        setContent {

            val navController = rememberNavController()
            val settingsViewModel = SettingsViewModel(navController, sharedPreferences)

            Scaffold(
                containerColor = Color(0xFF182354),
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    BottomNavigationBar(navController, currentRoute ?: "")
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    NavigationGraph(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        favoriteViewModel = favoriteViewModel,
                        settingsViewModel = settingsViewModel,
                        alertViewModel = alertViewModel
                    )

                }
            }
        }
    }

}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    favoriteViewModel: FavoriteViewModel,
    settingsViewModel: SettingsViewModel,
    alertViewModel: AlertViewModel
) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(homeViewModel) }
        composable(BottomNavItem.Favorite.route) {
            FavoriteScreen(
                favoriteViewModel,
                navController
            )
        }
        composable(BottomNavItem.Alert.route) { AlertScreen(alertViewModel) }
        composable(BottomNavItem.Settings.route) { SettingsScreen(settingsViewModel) }
        composable("map_screen_from_fav") {
            MapScreen(
                fromSetting = false,
                navController = navController,
                favViewModel = favoriteViewModel
            )
        }
        composable("map_screen_from_settings") {
            MapScreen(
                fromSetting = true,
                navController = navController,
                favViewModel = favoriteViewModel
            )
        }
    }
}

fun applyAppLocale(context: Context): Context {
    val sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE)
    val lang = sharedPreferences.getString(SettingsHelper.APP_LANGUAGE_PREF, "Default") ?: "Default"
    Log.i("TAG", "applyAppLocale: $lang")

    val locale = when (lang) {
        "Arabic" -> Locale("ar")
        "English" -> Locale("en")
        else -> Locale.getDefault()
    }

    Log.i("TAG", "applyAppLocale: ${locale.language}")
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)

    return context.createConfigurationContext(config)
}

