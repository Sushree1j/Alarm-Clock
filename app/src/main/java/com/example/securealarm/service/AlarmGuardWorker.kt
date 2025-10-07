package com.example.securealarm.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.alarm.AlarmConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AlarmGuardWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val app = applicationContext.applicationContext as? SecureAlarmApplication
            ?: return@withContext Result.retry()
        val repository = app.container.alarmRepository
        val activeAlarms = repository.getActiveAlarms()
        val now = System.currentTimeMillis()
        val dueAlarms = activeAlarms.filter { alarm ->
            alarm.isActive && alarm.triggerAtMillis <= now
        }
        if (dueAlarms.isEmpty()) {
            Timber.d("AlarmGuardWorker: No pending alarms to restore")
            return@withContext Result.success()
        }
        dueAlarms.forEach { alarm ->
            Timber.w("AlarmGuardWorker: Restarting alarm service for alarm ${'$'}{alarm.id}")
            val intent = Intent(applicationContext, AlarmService::class.java).apply {
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
            }
            ContextCompat.startForegroundService(applicationContext, intent)
        }
        Result.success()
    }

    companion object {
        private const val WORK_UNIQUE_NAME = "secure_alarm_guard"

        fun schedule(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val request = OneTimeWorkRequestBuilder<AlarmGuardWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .build()
            workManager.enqueueUniqueWork(WORK_UNIQUE_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}
