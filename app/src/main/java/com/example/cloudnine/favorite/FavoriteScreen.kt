package com.example.cloudnine.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.dataSource.local.model.FavoriteCity

@Composable
fun FavoriteScreen(favoriteViewModel: FavoriteViewModel, navController: NavController) {
    val favoriteCities = favoriteViewModel.favCitiesResponse.collectAsStateWithLifecycle().value
    favoriteViewModel.getAllFavCities()

    val listState = rememberLazyListState()
    var isFabVisible = remember { mutableStateOf(true) }
    var previousScrollOffset = remember { mutableStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { currentOffset ->
                isFabVisible.value = currentOffset < previousScrollOffset.value
                previousScrollOffset.value = currentOffset
            }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible.value,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("map_screen_from_fav") }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add City")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            when (favoriteCities) {
                is Response.Loading -> {
                    CircularProgressIndicator()
                }

                is Response.Success -> {
                    LazyColumn {
                        items(favoriteCities.data?.size ?: 0) { index ->
                            if (favoriteCities.data?.get(index) != null){
                                CityItem(
                                    favoriteCities.data[index], onClick = {
                                        // todo
                                    }, onDeleteClick = {
                                        //todo
                                    }
                                )
                            }
                        }
                    }
                }

                is Response.Failure -> {
                    Text("No Favorite Cities", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun CityItem(city: FavoriteCity, onClick: () -> Unit, onDeleteClick: (FavoriteCity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color(0xFF424242))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(city.cityName, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "Lat: ${city.latitude}, Lon: ${city.longitude}",
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
                }            }
        }
    }
}

