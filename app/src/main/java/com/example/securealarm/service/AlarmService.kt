package com.example.securealarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.securealarm.R
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.alarm.AlarmConstants
import com.example.securealarm.alarm.AlarmScheduler
import com.example.securealarm.alarm.AlarmTimeCalculator
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.data.local.AlarmEntity
import com.example.securealarm.ui.dismiss.AlarmDismissalActivity
import com.example.securealarm.ui.overlay.AlarmOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class AlarmService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val repository: AlarmRepository by lazy {
        (applicationContext as SecureAlarmApplication).container.alarmRepository
    }

    private val scheduler: AlarmScheduler by lazy {
        (applicationContext as SecureAlarmApplication).container.alarmScheduler
    }

    private var mediaPlayer: MediaPlayer? = null
    private var lastStopAuthorized: Boolean = false

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val alarmId = intent?.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1L) ?: -1L
        if (alarmId <= 0L) {
            Timber.w("AlarmService started without valid alarm id")
            stopSelf()
            return START_NOT_STICKY
        }
        when (action) {
            AlarmConstants.ACTION_STOP_REQUEST -> handleStopRequest(alarmId)
            AlarmConstants.ACTION_SNOOZE_REQUEST -> handleSnoozeRequest(alarmId)
            AlarmConstants.ACTION_AUTHORIZED_DISMISS -> handleAuthorizedDismiss(alarmId)
            else -> startAlarmFlow(alarmId)
        }
        return START_STICKY
    }

    private fun startAlarmFlow(alarmId: Long) {
        serviceScope.launch {
            val alarm = repository.getAlarm(alarmId)
            if (alarm == null) {
                Timber.w("Alarm not found for id $alarmId")
                stopSelf()
                return@launch
            }
            startForeground(AlarmConstants.NOTIFICATION_ID, buildNotification(alarm))
            playSound(alarm)
            launchOverlay(alarmId)
            scheduleNext(alarm)
        }
    }

    private fun handleStopRequest(alarmId: Long) {
        launchDismissal(alarmId)
    }

    private fun handleSnoozeRequest(alarmId: Long) {
        serviceScope.launch {
            val alarm = repository.getAlarm(alarmId) ?: return@launch
            val newTrigger = System.currentTimeMillis() + alarm.snoozeMinutes * 60_000L
            val updated = alarm.copy(triggerAtMillis = newTrigger)
            repository.updateAlarm(updated)
            scheduler.schedule(updated)
            stopAlarm(authorized = true)
        }
    }

    private fun handleAuthorizedDismiss(alarmId: Long) {
        serviceScope.launch {
            val alarm = repository.getAlarm(alarmId)
            if (alarm != null && alarm.repeatPattern == null) {
                repository.setActive(alarmId, false)
            }
            stopAlarm(authorized = true)
        }
    }

    private fun launchOverlay(alarmId: Long) {
        val overlayIntent = Intent(applicationContext, AlarmOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
        startActivity(overlayIntent)
    }

    private fun launchDismissal(alarmId: Long) {
        val dismissIntent = Intent(applicationContext, AlarmDismissalActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
        startActivity(dismissIntent)
    }

    private fun scheduleNext(alarm: AlarmEntity) {
        val nextTrigger = AlarmTimeCalculator.nextTriggerMillis(alarm.triggerAtMillis, alarm.repeatPattern)
        if (nextTrigger != null) {
            val updated = alarm.copy(triggerAtMillis = nextTrigger)
            serviceScope.launch {
                repository.updateAlarm(updated)
                scheduler.schedule(updated)
            }
        } else {
            serviceScope.launch {
                repository.setActive(alarm.id, false)
            }
        }
    }

    private fun playSound(alarm: AlarmEntity) {
        val soundUri = alarm.soundUri?.let { Uri.parse(it) } ?: Settings.System.DEFAULT_ALARM_ALERT_URI
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(applicationContext, soundUri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun buildNotification(alarm: AlarmEntity): Notification {
        val dismissIntent = PendingIntent.getService(
            this,
            (alarm.id * 10).toInt(),
            Intent(this, AlarmService::class.java).apply {
                action = AlarmConstants.ACTION_STOP_REQUEST
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val snoozeIntent = PendingIntent.getService(
            this,
            (alarm.id * 10 + 1).toInt(),
            Intent(this, AlarmService::class.java).apply {
                action = AlarmConstants.ACTION_SNOOZE_REQUEST
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val contentIntent = PendingIntent.getActivity(
            this,
            alarm.id.toInt(),
            Intent(this, AlarmDismissalActivity::class.java).apply {
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val contentDescription = alarm.label ?: getString(R.string.alarm_in_progress)
        val securityMessage = getString(R.string.security_protected)

        return NotificationCompat.Builder(this, AlarmConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(getString(R.string.alarm_active))
            .setContentText("$contentDescription • $securityMessage")
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, getString(R.string.stop_alarm), dismissIntent)
            .addAction(R.drawable.ic_snooze, getString(R.string.snooze), snoozeIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSubText(securityMessage)
            .build()
    }

    private fun ensureNotificationChannel() {
        val channel = NotificationChannel(
            AlarmConstants.NOTIFICATION_CHANNEL_ID,
            getString(R.string.alarm_active),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.alarm_in_progress)
            setShowBadge(true)
            enableVibration(true)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        if (mediaPlayer != null && !lastStopAuthorized) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        super.onDestroy()
        if (!lastStopAuthorized) {
            AlarmGuardWorker.schedule(applicationContext)
        }
        serviceScope.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!lastStopAuthorized) {
            AlarmGuardWorker.schedule(applicationContext)
        }
    }

    private fun stopAlarm(authorized: Boolean = false) {
        lastStopAuthorized = authorized
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}
