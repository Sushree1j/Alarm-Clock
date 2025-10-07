package com.example.securealarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityEventDao {

    @Insert
    suspend fun insert(event: SecurityEventEntity): Long

    @Query("SELECT * FROM security_events ORDER BY createdAt DESC")
    fun observeEvents(): Flow<List<SecurityEventEntity>>

    @Query("DELETE FROM security_events")
    suspend fun clear()
}
