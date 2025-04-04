package com.example.cloudnine.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertUnixTimestampToDateTime(timestamp: Long, apiLanguage: String): String {
    val date = Date(timestamp * 1000)
    when {
        apiLanguage.equals("Arabic", ignoreCase = true) -> {
            val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm a", Locale("ar"))
            return format.format(date)
        }
        apiLanguage.equals("English", ignoreCase = true) -> {
            val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm a", Locale.ENGLISH)
            return format.format(date)
        }
        else -> {
            val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm a", Locale.getDefault())
            return format.format(date)
        }
    }
}