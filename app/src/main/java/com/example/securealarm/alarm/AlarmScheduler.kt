package com.example.securealarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.securealarm.data.local.AlarmEntity
import com.example.securealarm.receiver.AlarmReceiver

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: AlarmEntity) {
        val pendingIntent = pendingIntentFor(alarm.id)
        val triggerAt = alarm.triggerAtMillis
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }

    fun cancel(alarm: AlarmEntity) {
        val pendingIntent = pendingIntentFor(alarm.id)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun pendingIntentFor(id: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmConstants.ACTION_OVERLAY
            putExtra(AlarmConstants.EXTRA_ALARM_ID, id)
        }
        return PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
