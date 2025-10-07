package com.example.securealarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [AlarmEntity::class, SecurityEventEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun securityEventDao(): SecurityEventDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getInstance(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "secure_alarm.db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
