package com.example.cloudnine.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorite,
        BottomNavItem.Alert,
        BottomNavItem.Settings
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(item.icon), contentDescription = stringResource(item.title)) },
                label = { Text(stringResource(item.title)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}