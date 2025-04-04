package com.example.cloudnine.settings

import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.navigation.NavController
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
            if (Locale.getDefault().language.equals("ar", ignoreCase = true)){
                sharedPreferences.edit() {putString(SettingsHelper.API_LANGUAGE_PREF, "Arabic")}
            }else {
                sharedPreferences.edit()  {putString(SettingsHelper.API_LANGUAGE_PREF, "English")}
            }
        } else {
            sharedPreferences.edit { putString(SettingsHelper.API_LANGUAGE_PREF, value) }
        }
        sharedPreferences.edit() { putString(SettingsHelper.APP_LANGUAGE_PREF, value) }
        updateAppLocale(value)
    }

    fun updateAppLocale(language: String) {
        val locale = when (language) {
            "Arabic" -> Locale("ar")
            "English" -> Locale("en")
            "Default" -> Locale.getDefault()
            else -> Locale.getDefault()
        }

        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)

        val context = navController.context
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
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