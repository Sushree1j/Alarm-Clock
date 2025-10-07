package com.example.securealarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.securealarm.R
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.alarm.AlarmConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SecurityAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AlarmConstants.ACTION_SECURITY_ALERT) return
        val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1L)
        val attempts = intent.getIntExtra(EXTRA_ATTEMPTS, 0)
        if (alarmId <= 0L || attempts <= 0) return

        val app = context.applicationContext as? SecureAlarmApplication ?: return
        val repository = app.container.alarmRepository
        val notificationManager = NotificationManagerCompat.from(context)
        ensureChannel(context)

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = repository.getAlarm(alarmId)
            val title = context.getString(R.string.security_alert_title)
            val message = if (alarm != null) {
                context.getString(
                    R.string.security_alert_description_with_label,
                    alarm.label ?: context.getString(R.string.alarm_in_progress),
                    attempts
                )
            } else {
                context.getString(R.string.security_alert_description_generic, attempts)
            }
            val notification = NotificationCompat.Builder(context, AlarmConstants.SECURITY_ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(AlarmConstants.SECURITY_ALERT_NOTIFICATION_ID, notification)
            Timber.w("Security alert triggered for alarm ${'$'}alarmId after ${'$'}attempts failed attempts")
        }
    }

    private fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            AlarmConstants.SECURITY_ALERT_CHANNEL_ID,
            context.getString(R.string.security_alert_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.security_alert_channel_description)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val EXTRA_ATTEMPTS = "extra_attempts"
    }
}
