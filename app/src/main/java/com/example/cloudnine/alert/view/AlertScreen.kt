package com.example.cloudnine.alert.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudnine.R
import com.example.cloudnine.AlarmManager.AlarmScheduler
import com.example.cloudnine.alert.viewModel.AlertViewModel
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import com.example.cloudnine.utils.convertTimestampToDateTime
import java.util.Calendar
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(alertViewModel: AlertViewModel) {
    val context = LocalContext.current
    val favCitiesState = alertViewModel.favCitiesResponse.collectAsState().value
    val alarmsState = alertViewModel.alarmResponse.collectAsState().value
    var selectedCity = remember { mutableStateOf<FavoriteCity?>(null) }

    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        alertViewModel.getAllFavCities()
    }
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet.value = true }) {
                Icon(painter = painterResource(R.drawable.add_alert), contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        when (alarmsState) {
            is Response.Loading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }

            is Response.Failure -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.error_fetching_alarms_from_database))
            }

            is Response.Success -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (alarmsState.data != null && alarmsState.data.isNotEmpty()){
                    LazyColumn {
                        itemsIndexed(alarmsState.data) { index, item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${stringResource(R.string.city)}: ${item.city}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${stringResource(R.string.latitude)}: ${item.lat}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${stringResource(R.string.longitude)}: ${item.lon}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(

                                        text = "${stringResource(R.string.triggered_time)}: ${
                                            convertTimestampToDateTime(
                                                item.triggerTimeInMillis,
                                                apiLanguage = alertViewModel.langPref,
                                                isEpoch = true
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                alertViewModel.deleteAlarm(item)
                                                AlarmScheduler(context).cancelAlarm(item.id)
                                            },
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
                    }
                } else {
                    Text(
                        stringResource(R.string.no_scheduled_alarms),
                        color = Color.White,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = bottomSheetState
            ) {
                when (favCitiesState) {
                    is Response.Success -> {
                        val cities = favCitiesState.data ?: emptyList()

                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                stringResource(R.string.select_a_city),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            cities.forEach { city ->
                                Text(
                                    text = city.cityName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCity.value = city
                                            showBottomSheet.value = false
                                            showDateTimePickers(
                                                context = context,
                                                city = city,
                                                onAlarmReady = { alarm ->
                                                    alertViewModel.insertAlarm(alarm)
                                                    AlarmScheduler(context).scheduleAlarm(alarm)
                                                    Toast.makeText(
                                                        context,
                                                        "Alarm Scheduled!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }

                    is Response.Loading -> {
                        Box(Modifier.padding(16.dp)) {
                            CircularProgressIndicator()
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
    }
}

fun showDateTimePickers(
    context: Context,
    city: FavoriteCity,
    onAlarmReady: (AlarmModel) -> Unit
) {
    val now = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selectedTime = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                    }

                    val triggerMillis = selectedTime.timeInMillis
                    Log.i("TAG", "showDateTimePickers: $triggerMillis")
                    Log.i("TAG", "showDateTimePickers: ${System.currentTimeMillis()}")
                    Log.i(
                        "TAG",
                        "showDateTimePickers: ${
                            convertTimestampToDateTime(
                                System.currentTimeMillis(),
                                "English",
                                isEpoch = true
                            )
                        }"
                    )
                    if (triggerMillis > System.currentTimeMillis()) {
                        val alarm = AlarmModel(
                            id = Random.nextInt(),
                            city = city.cityName,
                            lat = city.latitude,
                            lon = city.longitude,
                            triggerTimeInMillis = triggerMillis
                        )
                        onAlarmReady(alarm)
                    } else {

                        Toast.makeText(
                            context,
                            context.getString(R.string.please_pick_a_future_time),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            ).show()
        },
        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH)
    ).show()
}