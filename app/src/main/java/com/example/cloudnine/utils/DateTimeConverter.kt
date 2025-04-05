package com.example.cloudnine.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertTimestampToDateTime(timestamp: Long, apiLanguage: String, isEpoch : Boolean= false): String {
    val date = when(isEpoch){
        false -> Date(timestamp * 1000)
        true -> Date(timestamp)
    }
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