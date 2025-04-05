package com.example.cloudnine.alert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: AlarmModel) {
        Log.i("TAG", "scheduleAlarm: ${alarm.id}")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("CITY", alarm.city)
            putExtra("LAT", alarm.lat)
            putExtra("LON", alarm.lon)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.triggerTimeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    fun snoozeAlarm(alarm: AlarmModel, delayMillis: Long = 10000) {
        val snoozedTime = System.currentTimeMillis() + delayMillis
        val newAlarm = AlarmModel(id = alarm.id, city = alarm.city, lat = alarm.lat, lon = alarm.lon, triggerTimeInMillis = snoozedTime)
        Log.i("TAG", "snoozeAlarm: ${alarm.triggerTimeInMillis} and ${newAlarm.triggerTimeInMillis}")
        scheduleAlarm(newAlarm)
    }
}