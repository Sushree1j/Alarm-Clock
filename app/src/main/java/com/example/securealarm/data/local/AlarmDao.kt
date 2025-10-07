package com.example.securealarm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY trigger_at ASC")
    fun getAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Query("SELECT * FROM alarms WHERE is_active = 1")
    suspend fun getActiveAlarms(): List<AlarmEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity): Long

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("UPDATE alarms SET is_active = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}
