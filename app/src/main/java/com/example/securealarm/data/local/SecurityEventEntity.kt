package com.example.securealarm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_events")
data class SecurityEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alarmId: Long?,
    val eventType: String,
    val message: String?,
    val createdAt: Long
)
