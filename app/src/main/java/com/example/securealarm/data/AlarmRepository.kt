package com.example.securealarm.data

import com.example.securealarm.data.local.AlarmDao
import com.example.securealarm.data.local.AlarmEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepository(private val alarmDao: AlarmDao) {

    fun observeAlarms(): Flow<List<AlarmEntity>> = alarmDao.getAlarms()

    suspend fun getAlarm(id: Long): AlarmEntity? = alarmDao.getAlarmById(id)

    suspend fun getActiveAlarms(): List<AlarmEntity> = alarmDao.getActiveAlarms()

    suspend fun createAlarm(alarm: AlarmEntity): Long = alarmDao.insert(alarm)

    suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.update(alarm)

    suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.delete(alarm)

    suspend fun setActive(id: Long, active: Boolean) = alarmDao.setActive(id, active)

    fun observeActiveAlarms(): Flow<List<AlarmEntity>> = alarmDao.getAlarms().map { alarms ->
        alarms.filter { it.isActive }
    }
}
