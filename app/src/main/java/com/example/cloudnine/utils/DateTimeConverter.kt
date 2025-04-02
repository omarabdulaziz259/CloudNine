package com.example.cloudnine.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertUnixTimestampToDateTime(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm a", Locale.getDefault())
    return format.format(date)
}