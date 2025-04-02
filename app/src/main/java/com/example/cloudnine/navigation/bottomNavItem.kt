package com.example.cloudnine.navigation

import com.example.cloudnine.R

sealed class BottomNavItem(val route: String, val title: String, val icon: Int) {
    object Home : BottomNavItem("home", "Home", R.drawable.home)
    object Favorite : BottomNavItem("favorite", "Favorite", R.drawable.favorite)
    object Alert : BottomNavItem("alert", "Alert", R.drawable.notifications)
    object Settings : BottomNavItem("settings", "Settings", R.drawable.settings)
}