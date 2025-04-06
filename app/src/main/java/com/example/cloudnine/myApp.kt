package com.example.cloudnine

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import androidx.core.net.toUri

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
            val soundUri = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.thunder_sound}".toUri()
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
                description = "This channel is used for weather alert alarms"
                setSound(soundUri, audioAttributes)
            }

            val notificationManager: android.app.NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}

