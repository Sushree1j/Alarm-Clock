package com.example.securealarm.data.local

import androidx.room.TypeConverter
import com.example.securealarm.security.AuthMethod

class Converters {
    @TypeConverter
    fun fromAuthMethod(method: AuthMethod): String = method.name

    @TypeConverter
    fun toAuthMethod(value: String): AuthMethod = AuthMethod.valueOf(value)
}
