package com.example.cloudnine.settings.viewModel

import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.cloudnine.settings.SettingsHelper
import java.util.Locale

class SettingsViewModel(
    val navController: NavController,
    val sharedPreferences: SharedPreferences
) {
    var selectedLanguage = mutableStateOf(getSavedLanguage())
    var selectedTempUnit = mutableStateOf(getSavedTempUnit())
    var selectedLocation = mutableStateOf(getSavedLocationPref())


    fun saveLanguage(value: String) {
        selectedLanguage.value = value
        if (value == "Default"){
            val systemLang = Locale.getDefault().language
            sharedPreferences.edit {
                putString(
                    SettingsHelper.API_LANGUAGE_PREF,
                    if (systemLang.equals("ar", ignoreCase = true)) "Arabic" else "English"
                )
            }
        } else {
            sharedPreferences.edit { putString(SettingsHelper.API_LANGUAGE_PREF, value) }
        }
        sharedPreferences.edit() { putString(SettingsHelper.APP_LANGUAGE_PREF, value) }
        restartActivity()
    }

    fun restartActivity() {
        val context = navController.context
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
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
        return sharedPreferences.getString(SettingsHelper.APP_LANGUAGE_PREF, "Default") ?: "Default"
    }

    fun getSavedTempUnit(): String {
        return sharedPreferences.getString(SettingsHelper.TEMP_UNIT_PREF, "Kelvin") ?: "Kelvin"
    }

    fun getSavedLocationPref(): String {
        return sharedPreferences.getString(SettingsHelper.LOCATION_PREF, "GPS") ?: "GPS"
    }

    fun navigateToMapScreen() {
        navController.navigate("map_screen_from_settings")
    }
}