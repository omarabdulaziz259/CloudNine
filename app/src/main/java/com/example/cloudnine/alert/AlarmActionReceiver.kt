package com.example.cloudnine.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val action = intent.action ?: return

        val weatherRepository = WeatherRepository.getInstance(context)


        CoroutineScope(Dispatchers.IO).launch {
            val alarm = weatherRepository.getLocalAlarmById(alarmId) ?: return@launch
            val scheduler = AlarmScheduler(context)

            when (action) {
                "SNOOZE" -> scheduler.snoozeAlarm(alarm)
                "DONE" -> {
                    scheduler.cancelAlarm(alarmId)
                    weatherRepository.deleteLocalAlarmById(alarmId)
                }
            }
        }

        NotificationManagerCompat.from(context).cancel(alarmId)
    }
}