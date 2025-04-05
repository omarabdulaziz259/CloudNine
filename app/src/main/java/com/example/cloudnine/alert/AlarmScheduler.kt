package com.example.cloudnine.alert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: AlarmModel) {
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

    fun snoozeAlarm(alarm: AlarmModel, delayMillis: Long = 60000) {
        val snoozedTime = System.currentTimeMillis() + delayMillis
        val snoozedAlarm = alarm.copy(triggerTimeInMillis = snoozedTime)
        scheduleAlarm(snoozedAlarm)
    }
}