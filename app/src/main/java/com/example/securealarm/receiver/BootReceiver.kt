package com.example.securealarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.alarm.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }
        val pendingResult = goAsync()
        val app = context.applicationContext as SecureAlarmApplication
        val repository = app.container.alarmRepository
        val scheduler: AlarmScheduler = app.container.alarmScheduler
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                repository.getActiveAlarms().forEach { scheduler.schedule(it) }
            }.onFailure { Timber.e(it, "Failed to reschedule alarms on boot") }
            pendingResult.finish()
        }
    }
}
