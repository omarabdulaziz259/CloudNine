package com.example.cloudnine.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cloudnine.R

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    var selectedLanguage = settingsViewModel.selectedLanguage
    var selectedTempUnit = settingsViewModel.selectedTempUnit
    var selectedLocation = settingsViewModel.selectedLocation

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.language), style = MaterialTheme.typography.titleLarge)
        RadioGroup(
            options = listOf(
                stringResource(R.string.arabic),
                stringResource(R.string.english),
                stringResource(R.string.default_)
            ),
            selectedOption = when(selectedLanguage.value) {
                "Arabic" -> stringResource(R.string.arabic)
                "English" -> stringResource(R.string.english)
                "Default" -> stringResource(R.string.default_)
                else -> stringResource(R.string.default_)
            },
            onOptionSelected = {
                when(it){
                    "العربية" -> settingsViewModel.saveLanguage("Arabic")
                    "الإنجليزية" -> settingsViewModel.saveLanguage("English")
                    "الافتراضية" -> settingsViewModel.saveLanguage("Default")
                    else -> settingsViewModel.saveLanguage(it)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.temperature_unit), style = MaterialTheme.typography.titleLarge)
        RadioGroup(
            options = listOf(
                stringResource(R.string.kelvin),
                stringResource(R.string.fahrenheit),
                stringResource(R.string.celsius)
            ),
            selectedOption = when(selectedTempUnit.value){
                "Kelvin" -> stringResource(R.string.kelvin)
                "Fahrenheit" -> stringResource(R.string.fahrenheit)
                "Celsius" -> stringResource(R.string.celsius)
                else -> stringResource(R.string.kelvin)
            },
            onOptionSelected = {
                when(it){
                    "كلفن" -> settingsViewModel.saveTempUnit("Kelvin")
                    "فهرنهايت" -> settingsViewModel.saveTempUnit("Fahrenheit")
                    "سيلسيوس" -> settingsViewModel.saveTempUnit("Celsius")
                    else -> settingsViewModel.saveTempUnit(it)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.location_based), style = MaterialTheme.typography.titleLarge)
        RadioGroup(
            options = listOf(
                stringResource(R.string.gps),
                stringResource(R.string.manual)
            ),
            selectedOption = when(selectedLocation.value){
                "GPS" -> stringResource(R.string.gps)
                "Manual" -> stringResource(R.string.manual)
                else -> stringResource(R.string.gps)
            },
            onOptionSelected = {
                when(it){
                    "نظام تحديد المواقع (GPS)" -> settingsViewModel.saveLocationPref("GPS")
                    "يدوي" -> settingsViewModel.saveLocationPref("Manual")
                    else -> settingsViewModel.saveLocationPref(it)
                }
            }
        )

        if (selectedLocation.value == stringResource(R.string.manual)) {
            Button(
                onClick = { settingsViewModel.navigateToMapScreen() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.choose_location_on_map))
            }
        }
    }
}

@Composable
fun RadioGroup(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column {
        options.forEach { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) })
                    .padding(8.dp)
            ) {
                RadioButton(selected = (text == selectedOption), onClick = { onOptionSelected(text) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(text)
            }
        }
    }
}
