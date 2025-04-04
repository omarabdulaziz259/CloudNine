package com.example.cloudnine.settings

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.navigation.NavController

class SettingsViewModel(val navController: NavController, val sharedPreferences: SharedPreferences) {
    var selectedLanguage = mutableStateOf(getSavedLanguage())
    var selectedTempUnit = mutableStateOf(getSavedTempUnit())
    var selectedLocation = mutableStateOf(getSavedLocationPref())




    fun saveLanguage(value: String) {
        selectedLanguage.value = value
        sharedPreferences.edit() { putString(SettingsHelper.LANGUAGE_PREF, value) }
    }

    fun saveTempUnit(value: String) {
        selectedTempUnit.value = value
        sharedPreferences.edit() { putString(SettingsHelper.TEMP_UNIT_PREF, value) }
    }

    fun saveLocationPref(value: String) {
        selectedLocation.value = value
        sharedPreferences.edit() { putString(SettingsHelper.LOCATION_PREF, value) }
    }

    fun getSavedLanguage(): String {
        return sharedPreferences.getString(SettingsHelper.LANGUAGE_PREF, "Default") ?: "Default"
    }

    fun getSavedTempUnit(): String {
        return sharedPreferences.getString(SettingsHelper.TEMP_UNIT_PREF, "Celsius") ?: "Celsius"
    }

    fun getSavedLocationPref(): String {
        return sharedPreferences.getString(SettingsHelper.LOCATION_PREF, "GPS") ?: "GPS"
    }

    fun navigateToMapScreen(){
        navController.navigate("map_screen_from_settings")
    }
}