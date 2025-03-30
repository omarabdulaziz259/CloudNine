package com.example.cloudnine.model

enum class TemperatureUnit(val apiValue: String?) {
    CELSIUS("metric"),
    FAHRENHEIT("imperial"),
    KELVIN(null);
}