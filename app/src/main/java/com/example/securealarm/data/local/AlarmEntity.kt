package com.example.securealarm.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.securealarm.security.AuthMethod

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "trigger_at")
    val triggerAtMillis: Long,
    @ColumnInfo(name = "repeat_pattern")
    val repeatPattern: String?,
    @ColumnInfo(name = "label")
    val label: String?,
    @ColumnInfo(name = "sound_uri")
    val soundUri: String?,
    @ColumnInfo(name = "auth_method")
    val authMethod: AuthMethod,
    @ColumnInfo(name = "auth_data")
    val authData: String,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "snooze_minutes")
    val snoozeMinutes: Int = 10
)
