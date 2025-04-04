package com.example.cloudnine.home

import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.WeatherResponse
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.settings.SettingsHelper
import com.example.cloudnine.utils.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationHelper: LocationHelper,
    private val sharedPreferences: SharedPreferences ) : ViewModel() {
    private val _weatherResponse: MutableStateFlow<Response<WeatherResponse>> =
        MutableStateFlow(Response.Loading)
    val weatherResponse = _weatherResponse.asStateFlow()

    private val _forecastResponse: MutableStateFlow<Response<ForecastResponse>> =
        MutableStateFlow(Response.Loading)
    val forecastResponse = _forecastResponse.asStateFlow()


    private val _locationState = MutableStateFlow<Location?>(null)

    val locationState = _locationState.asStateFlow()

    var unit: TemperatureUnit = TemperatureUnit.KELVIN

    var langPref = sharedPreferences.getString(SettingsHelper.API_LANGUAGE_PREF, "English") ?: "English"


    fun fetchWeatherBasedOnPreference() {
        val locationPref = sharedPreferences.getString(SettingsHelper.LOCATION_PREF, "GPS")
        langPref = sharedPreferences.getString(SettingsHelper.API_LANGUAGE_PREF, "English") ?: "English"
        val language = when (langPref) {
            "Arabic" -> Language.ARABIC
            "English" -> Language.ENGLISH
            else -> Language.ENGLISH
        }
        val unitsPref = sharedPreferences.getString(SettingsHelper.TEMP_UNIT_PREF, "Kelvin")
        val units = when (unitsPref) {
            "Kelvin" -> TemperatureUnit.KELVIN
            "Celsius" -> TemperatureUnit.CELSIUS
            "Fahrenheit" -> TemperatureUnit.FAHRENHEIT
            else -> TemperatureUnit.KELVIN
        }
        unit = units
        viewModelScope.launch {
            when (locationPref) {
                "GPS" -> {
                    initLocationHelper()
                    locationHelper.locationState.collect { location ->
                        location?.let {
                            _locationState.value = it
                            getCurrentDayWeather(it.longitude, it.latitude, language, units)
                            getDailyForecasts(it.longitude, it.latitude, language, units)

                        }
                    }
                }

                "Manual" -> {
                    val longPref = sharedPreferences.getFloat(SettingsHelper.LONG_PREF, 31.1f)
                    val latPref = sharedPreferences.getFloat(SettingsHelper.LAT_PREF, 31.2f)

                    getLocationFromPinnedLocation(
                        longPref.toDouble(),
                        latPref.toDouble(),
                        language,
                        units
                    )
                }
            }
        }
    }

    fun initLocationHelper() {
        viewModelScope.launch {
            locationHelper.locationState.collect { newLocation ->
                newLocation?.let {
                    _locationState.value = it
                }
            }
        }
    }

    fun getLocationFromPinnedLocation(
        lon: Double,
        lat: Double,
        language: Language,
        temperatureUnit: TemperatureUnit
    ) {
        if (lat != 0.0 && lon != 0.0) {
            getCurrentDayWeather(lon, lat, language, temperatureUnit)
            getDailyForecasts(lon, lat, language, temperatureUnit)
        }
    }

    fun getCurrentDayWeather(
        lon: Double, lat: Double, language: Language, temperatureUnit: TemperatureUnit
    ) {
        viewModelScope.launch {
            try {
                weatherRepository.getRemoteCurrentDayWeather(
                    lat = lat, lon = lon, temperatureUnit = temperatureUnit, language = language
                ).catch { error ->
                    _weatherResponse.emit(Response.Failure(error))
                }.collect {
                    _weatherResponse.emit(Response.Success<WeatherResponse>(it as WeatherResponse))
                }
            } catch (ex: Exception) {
                _weatherResponse.emit(Response.Failure(ex))
            }
        }
    }

    fun getDailyForecasts(
        lon: Double, lat: Double, language: Language, temperatureUnit: TemperatureUnit
    ) {
        viewModelScope.launch {
            try {
                weatherRepository.getRemoteDailyForecasts(
                    lat = lat, lon = lon, temperatureUnit = temperatureUnit, language = language
                ).catch { error ->
                    _forecastResponse.emit(Response.Failure(error))
                }.collect {
                    _forecastResponse.emit(Response.Success<ForecastResponse>(it as ForecastResponse))
                }
            } catch (ex: Exception) {
                _forecastResponse.emit(Response.Failure(ex))
            }
        }
    }
}