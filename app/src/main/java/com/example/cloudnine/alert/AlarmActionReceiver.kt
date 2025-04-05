package com.example.cloudnine.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.cloudnine.model.dataSource.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class AlarmActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        Log.i("TAG", "AlarmActionReceiver: $alarmId")
        val action = intent.action ?: return
        Log.i("TAG", "AlarmActionReceiver: $action")

        val weatherRepository = WeatherRepository.getInstance(context)


        CoroutineScope(Dispatchers.IO).launch {
            Log.i("TAG", "AlarmActionReceiver: inside coroutine1")
            val alarm = weatherRepository.getLocalAlarmById(alarmId) ?: return@launch
            Log.i("TAG", "AlarmActionReceiver: inside coroutine2")
            val scheduler = AlarmScheduler(context)
            Log.i("TAG", "AlarmActionReceiver: inside coroutine3")

            when (action) {
                "SNOOZE" -> {
                    Log.i("TAG", "onReceive: i am here inside snooze")
                    scheduler.snoozeAlarm(alarm)
                }
                "DONE" -> {
                    Log.i("TAG", "onReceive: i am here inside done")
                    scheduler.cancelAlarm(alarmId)
                    weatherRepository.deleteLocalAlarmById(alarmId)
                }
            }
        }

        NotificationManagerCompat.from(context).cancel(alarmId)
    }
}