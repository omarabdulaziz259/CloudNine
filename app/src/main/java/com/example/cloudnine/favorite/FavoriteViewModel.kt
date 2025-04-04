package com.example.cloudnine.favorite

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudnine.R
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.WeatherResponse
import com.example.cloudnine.model.dataSource.local.model.FavoriteCity
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.settings.SettingsHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class FavoriteViewModel(
    private val weatherRepository: WeatherRepository,
    val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _favCitiesResponse: MutableStateFlow<Response<List<FavoriteCity>?>> =
        MutableStateFlow(Response.Loading)

    val favCitiesResponse = _favCitiesResponse.asStateFlow()

    private val _forecastResponse: MutableStateFlow<Response<ForecastResponse>> =
        MutableStateFlow(Response.Loading)
    val forecastResponse = _forecastResponse.asStateFlow()

    private val _weatherResponse: MutableStateFlow<Response<WeatherResponse>> =
        MutableStateFlow(Response.Loading)
    val weatherResponse = _weatherResponse.asStateFlow()

    private val _message: MutableSharedFlow<Int?> = MutableSharedFlow()
    val message = _message.asSharedFlow()


    val langPref = sharedPreferences.getString("langPref", "ENGLISH")

    val language = when (langPref) {
        "ARABIC" -> Language.ARABIC
        "ENGLISH" -> Language.ENGLISH
        else -> Language.ENGLISH
    }

    val unitsPref = sharedPreferences.getString("unitsPref", "KELVIN")

    val units = when (unitsPref) {
        "KELVIN" -> TemperatureUnit.KELVIN
        "CELSIUS" -> TemperatureUnit.CELSIUS
        "FAHRENHEIT" -> TemperatureUnit.FAHRENHEIT
        else -> TemperatureUnit.KELVIN
    }

    fun getAllFavCities() {
        viewModelScope.launch {
            try {
                weatherRepository.getLocalAllFavoriteCities()
                    .catch {
                        _favCitiesResponse.emit(Response.Failure(it))
                    }.collect {
                        _favCitiesResponse.emit(Response.Success(it))
                    }
            } catch (e: Exception) {
                _favCitiesResponse.emit(Response.Failure(e))
            }
        }
    }

    fun insertFavCity(cityName: String, latitude: Double, longitude: Double) {
        val favoriteCity = FavoriteCity(cityName, latitude, longitude)

        viewModelScope.launch {
            try {
                val result = weatherRepository.addLocalFavoriteCity(favoriteCity)
                if (result > 0) {
                    _message.emit(R.string.city_added_successfully)
                } else {
                    _message.emit(R.string.error_failed_to_insert_city)
                }
            } catch (e: Exception) {
                _message.emit(R.string.error_failed_to_insert_city)
            }
        }
    }

    fun deleteFavCity(favoriteCity: FavoriteCity) {
        viewModelScope.launch {
            try {
                val result = weatherRepository.deleteLocalFavoriteCity(favoriteCity)
                if (result > 0) {
                    _message.emit(R.string.city_deleted_successfully)

                } else {
                    _message.emit(R.string.error_failed_to_delete_city)
                }
            } catch (e: Exception) {
                _message.emit(R.string.error_failed_to_delete_city)
            }
        }
    }

    fun getDailyForecasts(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit,
        language: Language
    ) {
        viewModelScope.launch {
            try {
                weatherRepository.getRemoteDailyForecasts(
                    lat = lat,
                    lon = lon,
                    temperatureUnit = temperatureUnit,
                    language = language
                ).catch {
                    _forecastResponse.emit(Response.Failure(it))
                }.collect {
                    _forecastResponse.emit(Response.Success<ForecastResponse>(it as ForecastResponse))
                }
            } catch (e: Exception) {
                _forecastResponse.emit(Response.Failure(e))
            }
        }
    }

    fun getForecastForCity(city: FavoriteCity){
        getDailyForecasts(lat = city.latitude, lon = city.longitude, temperatureUnit = units, language = language)
    }
    fun getWeatherForCity(city: FavoriteCity) {
        viewModelScope.launch {
            try {
                weatherRepository.getRemoteCurrentDayWeather(
                    lat = city.latitude,
                    lon = city.longitude,
                    temperatureUnit = units,
                    language = language
                ).catch { _weatherResponse.emit(Response.Failure(it)) }
                    .collect { _weatherResponse.emit(Response.Success<WeatherResponse>(it as WeatherResponse)) }
            } catch (e: Exception){ _weatherResponse.emit(Response.Failure(e))}
        }
    }


    fun saveManualLonLat(lon: Double, lat: Double){
        sharedPreferences.edit() {
            putFloat(SettingsHelper.LONG_PREF, lon.toFloat())
            putFloat(SettingsHelper.LAT_PREF, lat.toFloat())
        }
    }

    fun getCountryName(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses[0].countryName
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getCountryName(countryCode: String?): String {
        return Locale("", countryCode).displayCountry ?: "UnSpecified"
    }

}