package com.example.cloudnine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.dataSource.remote.WeatherRemoteDataSource
import com.example.cloudnine.model.WeatherResponse
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        Log.i("TAG", "onCreate: ${BuildConfig.API_KEY}")
        val weatherRemoteDataSource = WeatherRemoteDataSource.getInstance()
        var weatherResponse by mutableStateOf<WeatherResponse?>(null)
        lifecycleScope.launch {
            try {
                weatherRemoteDataSource.getCurrentDayWeather(
                    lon = 10.99,
                    lat = 44.34,
                    units = TemperatureUnit.KELVIN,
                    language = Language.ENGLISH
                ).collect { response ->
                    Log.i("MainActivity", "Weather Data: $response")
                    weatherResponse = response // Update state
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching weather", e)
            }
        }

        setContent {
            weatherResponse?.let {
                Text(text = it.name.toString())
            } ?: Text(text = "Loading...")
        }
    }
}