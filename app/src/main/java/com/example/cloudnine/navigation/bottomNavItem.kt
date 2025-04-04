package com.example.cloudnine.navigation

import com.example.cloudnine.R

sealed class BottomNavItem(val route: String, val title: Int, val icon: Int) {
    object Home : BottomNavItem("home", R.string.home, R.drawable.home)
    object Favorite : BottomNavItem("favorite",R.string.favorite, R.drawable.favorite)
    object Alert : BottomNavItem("alert", R.string.alert, R.drawable.notifications)
    object Settings : BottomNavItem("settings", R.string.settings, R.drawable.settings)
}