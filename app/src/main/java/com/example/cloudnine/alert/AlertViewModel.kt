package com.example.cloudnine.alert

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudnine.model.Response
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import com.example.cloudnine.settings.SettingsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val weatherRepository: WeatherRepository, val sharedPreferences: SharedPreferences) : ViewModel(){
    private val _favCitiesResponse: MutableStateFlow<Response<List<FavoriteCity>?>> =
        MutableStateFlow(Response.Loading)

    val favCitiesResponse = _favCitiesResponse.asStateFlow()

    private val _alarmResponse: MutableStateFlow<Response<List<AlarmModel>?>> =
        MutableStateFlow(Response.Loading)

    val alarmResponse = _alarmResponse.asStateFlow()

    var langPref = sharedPreferences.getString(SettingsHelper.API_LANGUAGE_PREF, "English") ?: "English"

    init {
        getAllAlarms()
    }

    fun getAllAlarms(){
        langPref = sharedPreferences.getString(SettingsHelper.API_LANGUAGE_PREF, "English") ?: "English"

        viewModelScope.launch {
            try {
                weatherRepository.getLocalAllAlarms()
                    .catch {
                        _alarmResponse.emit(Response.Failure(it))
                    }.collect {
                        _alarmResponse.emit(Response.Success(it))
                    }
            } catch (e: Exception) {
                _alarmResponse.emit(Response.Failure(e))
            }
        }
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

    fun insertAlarm(alarm: AlarmModel){
        viewModelScope.launch {
            weatherRepository.insertLocalAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: AlarmModel) = viewModelScope.launch {
        weatherRepository.deleteLocalAlarm(alarm)
    }

}