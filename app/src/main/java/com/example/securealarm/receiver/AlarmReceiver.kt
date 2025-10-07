package com.example.securealarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.securealarm.alarm.AlarmConstants
import com.example.securealarm.service.AlarmService
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1L)
        if (alarmId <= 0L) {
            Timber.w("AlarmReceiver received invalid id")
            return
        }
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
