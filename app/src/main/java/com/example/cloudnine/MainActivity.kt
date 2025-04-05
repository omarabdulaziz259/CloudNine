package com.example.cloudnine

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.example.cloudnine.favorite.FavoriteViewModel
import com.example.cloudnine.favorite.MapScreen
import com.example.cloudnine.home.HomeScreen
import com.example.cloudnine.home.HomeViewModel
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.navigation.BottomNavItem
import com.example.cloudnine.navigation.BottomNavigationBar
import com.example.cloudnine.settings.SettingsHelper
import com.example.cloudnine.settings.SettingsScreen
import com.example.cloudnine.settings.SettingsViewModel
import com.example.cloudnine.utils.LocationHelper
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        val localeUpdatedContext = newBase?.let { applyAppLocale(it) }
        super.attachBaseContext(localeUpdatedContext)

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
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
        locationHelper.getLocation()
        setContent {
            val navController = rememberNavController()
            val settingsViewModel = SettingsViewModel(navController, sharedPreferences)
            Scaffold(
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
                        settingsViewModel = settingsViewModel
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
    settingsViewModel: SettingsViewModel
) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(homeViewModel) }
        composable(BottomNavItem.Favorite.route) {
            FavoriteScreen(
                favoriteViewModel,
                navController
            )
        }
        composable(BottomNavItem.Alert.route) { AlertScreen() }
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

