package com.example.cloudnine.BroadcastReceiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.cloudnine.R
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.dataSource.remote.model.List1
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        Log.i("TAG", "AlarmReceiver: $alarmId")
        val city = intent.getStringExtra("CITY")
        val lat = intent.getDoubleExtra("LAT", 0.0)
        val lon = intent.getDoubleExtra("LON", 0.0)

        if (alarmId == -1) return

        val weatherRepository = WeatherRepository.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            val forecastData = mutableStateOf<ForecastResponse?>(null)
            weatherRepository.getRemoteDailyForecasts(lat, lon).collect {
                forecastData.value = it
            }
            forecastData.value?.let {
                val groupedForecast = it.list.groupBy { it.dtTxt?.substring(0, 10) }
                val todayKey = groupedForecast.keys.firstOrNull()
                val todayForecast = groupedForecast.get(todayKey) ?: emptyList()
                val message = createWeatherConditionMessage(todayForecast)
                showNotification(context, alarmId, city ?: "Unknown", message)
            }
        }
    }
}

fun createWeatherConditionMessage(todayForecast: List<List1>): String {
    val maxTemp = todayForecast.maxOfOrNull { it.main?.temp ?: 0.0 } ?: 0.0
    val minTemp = todayForecast.minOfOrNull { it.main?.temp ?: 0.0 } ?: 0.0
    val windSpeed = todayForecast.maxOfOrNull { it.wind?.speed ?: 0.0 } ?: 0.0

    val isHot = isHighTemperature(maxTemp)
    val isCold = isLowTemperature(minTemp)
    val isWindy = isHighWind(windSpeed)
    val isRain = isRaining(todayForecast)

    if (isHot || isCold || isWindy || isRain) {
        val messageBuilder = StringBuilder("Today brings ")

        when {
            isHot -> messageBuilder.append("hot weather with a high of $maxTemp°C")
            isCold -> messageBuilder.append("cold weather with a low of $minTemp°C")
            else -> messageBuilder.append("mild temperatures (high: $maxTemp°C, low: $minTemp°C)")
        }

        if (isWindy) {
            messageBuilder.append(" and strong winds of $windSpeed m/s")
        }

        if (isRain) {
            messageBuilder.append(" and possible rainfall")
        }

        messageBuilder.append(".")
        return messageBuilder.toString()
    }

    return "Good news, today's weather is normal with a high of $maxTemp°C and low of $minTemp°C."
}

fun isHighTemperature(maxTemp: Double): Boolean {
    return maxTemp >= 40
}

fun isLowTemperature(minTemp: Double): Boolean {
    return minTemp <= 5
}

fun isRaining(todayForecast: List<List1>): Boolean {
    todayForecast.forEach {
        it.rain?.let {
            return true
        }
    }
    return false
}

fun isHighWind(windSpeed: Double): Boolean {
    return windSpeed > 11.0
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showNotification(context: Context, alarmId: Int, city: String, weatherMsg: String) {
    val snoozeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
        action = "SNOOZE"
        putExtra("ALARM_ID", alarmId)
    }

    val doneIntent = Intent(context, AlarmActionReceiver::class.java).apply {
        action = "DONE"
        putExtra("ALARM_ID", alarmId)
    }

    val snoozePendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        snoozeIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val donePendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        doneIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val soundUri = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.thunder_sound}".toUri()

    val notification = NotificationCompat.Builder(context, "weather_alarm_channel")
        .setSmallIcon(R.drawable.notifications)
        .setContentTitle("Weather Alert in $city")
        .setContentText(weatherMsg)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(soundUri)
        .addAction(R.drawable.snooze, "Snooze", snoozePendingIntent)
        .addAction(R.drawable.done, "Done", donePendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(alarmId, notification)
}
