package com.example.cloudnine

import android.app.Application
import android.content.Context

class myApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "weather_alarm_channel"
            val channelName = "Weather Alarm Notifications"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
                description = "This channel is used for weather alert alarms"
            }

            val notificationManager: android.app.NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}

