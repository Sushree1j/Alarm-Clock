package com.example.securealarm.alarm

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object AlarmTimeCalculator {

    fun nextTriggerMillis(currentTrigger: Long, repeatPattern: String?): Long? {
        val zoneId = ZoneId.systemDefault()
        val triggerDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTrigger), zoneId)
        return when {
            repeatPattern.isNullOrBlank() -> null
            repeatPattern.equals("DAILY", ignoreCase = true) -> triggerDateTime.plusDays(1).atZone(zoneId).toInstant().toEpochMilli()
            repeatPattern.startsWith("WEEKLY:", ignoreCase = true) -> {
                val days = repeatPattern.substringAfter(':')
                    .split(',')
                    .mapNotNull { it.toIntOrNull() }
                    .mapNotNull { runCatching { DayOfWeek.of(it) }.getOrNull() }
                    .sorted()
                if (days.isEmpty()) return null
                val currentDay = triggerDateTime.dayOfWeek
                val nextDay = days.firstOrNull { it.value > currentDay.value } ?: days.first()
                val additionalDays = if (nextDay.value > currentDay.value) {
                    (nextDay.value - currentDay.value).toLong()
                } else {
                    (7 - currentDay.value + nextDay.value).toLong()
                }
                triggerDateTime.plusDays(additionalDays).atZone(zoneId).toInstant().toEpochMilli()
            }
            else -> null
        }
    }
}
