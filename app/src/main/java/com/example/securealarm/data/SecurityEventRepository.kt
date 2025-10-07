package com.example.securealarm.data

import com.example.securealarm.data.local.SecurityEventDao
import com.example.securealarm.data.local.SecurityEventEntity
import com.example.securealarm.security.SecurityEventType
import kotlinx.coroutines.flow.Flow

class SecurityEventRepository(private val dao: SecurityEventDao) {

    suspend fun recordEvent(alarmId: Long?, type: SecurityEventType, message: String? = null) {
        val entity = SecurityEventEntity(
            alarmId = alarmId,
            eventType = type.name,
            message = message,
            createdAt = System.currentTimeMillis()
        )
        dao.insert(entity)
    }

    fun observeEvents(): Flow<List<SecurityEventEntity>> = dao.observeEvents()

    suspend fun clear() = dao.clear()
}
